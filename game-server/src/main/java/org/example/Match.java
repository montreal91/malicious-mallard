package org.example;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

/**
 * Objects of this class should live inside one thread (MatchRunner)
 * and should not be accessed by any other threads.
 */
public class Match {
    private static final int MAX_ACTIONS_PER_TICK = 10;

    private final String id; // String is temporary, should be UUID
    private long lastUpdateAt;

    private final Map<Integer, Team> teamStorage = new LinkedHashMap<>(); // This list should be immutable
    private final Map<Integer, Player> playerStorage = new LinkedHashMap<>(); // This list should be immutable
    private final Map<Integer, BaseUnit> unitStorage = new LinkedHashMap<>(); // But this list is mutable

    private Team definedWinner;
    private boolean isOver = false;

    private final Meta meta = new Meta();

    private final Queue<BaseAction> inputQueue;
    private final Queue<MatchStateSnapshot> outputQueue;

    public Match(
            String id,
            InitialState initialState,
            Queue<BaseAction> inputQueue,
            Queue<MatchStateSnapshot> outputQueue
    ) {
        this.id = Objects.requireNonNull(id, "ID cannot be null");
        this.inputQueue = Objects.requireNonNull(inputQueue, "Input queue cannot be null");
        this.outputQueue = Objects.requireNonNull(outputQueue, "Output queue cannot be null");
        Objects.requireNonNull(initialState, "Initial state cannot be null");

        for (var team : initialState.teamList()) {
            teamStorage.put(team.id(), team);
        }

        for (var player : initialState.playerList()) {
            playerStorage.put(player.id(), player);
        }

        for (var unit : initialState.unitList()) {
            unitStorage.put(unit.getId(), unit);
        }

        lastUpdateAt = Long.MAX_VALUE;
    }

    public String getId() {
        return id;
    }

    public void start() {
        meta.start();
        lastUpdateAt = System.currentTimeMillis();
    }

    public void end() {
        meta.end();
    }

    /**
     * Updates game state and does some bookkeeping.
     *
     * @param dt delta time
     */
    public void update(long dt) {
        // Bookkeeping part
        long updateStart = System.currentTimeMillis();

        long timeSinceLastUpdate = Math.max(updateStart - lastUpdateAt, 0);
        meta.addDrift(Math.abs(timeSinceLastUpdate - dt));
        meta.tick(dt);

        lastUpdateAt = updateStart;

        // Yes, actual update logic is here
        actualUpdate(dt);
        publishUpdate();

        // More bookkeeping
        long updateEnd = System.currentTimeMillis();
        meta.addUpdateTime(updateEnd - updateStart);
    }

    /**
     * Returns the winner of the match, if it has any.
     *
     * @return the winner of the match.
     * If match is over and the winner is null it means that the match is tied.
     * In RTS it should be a very rare case.
     */
    public Optional<MatchResult> getResult() {
        if (definedWinner == null) {
            // Probably, this case will never happen, just in case.
            return Optional.empty();
        }

        return Optional.of(new MatchResult(definedWinner, meta));
    }

    /**
     * Match is over if (and only if) all players left it.
     *
     * @return if match over or not
     */
    public boolean isOver() {
        return isOver;
    }

    public Meta getMeta() {
        return meta;
    }

    private void actualUpdate(long dt) {
        if (isOver) {
            return;
        }

        applyInput();

        // Later here will be some additional steps before publishing new update
    }

    private void publishUpdate() {
        outputQueue.add(makeSnapshot());
    }

    private MatchStateSnapshot makeSnapshot() {
        var tl = new ArrayList<TeamStateSnapshot>();

        for (Team t : teamStorage.values()) {
            tl.add(new TeamStateSnapshot(t.id()));
        }

        var pll = new ArrayList<PlayerStateSnapshot>();
        var sl = new ArrayList<SelectionStateSnapshot>();

        for (Player p : playerStorage.values()) {
            pll.add(new PlayerStateSnapshot(
                    p.id(),
                    p.teamId(),
                    p.name(),
                    p.status().toString()
            ));

            sl.add(new SelectionStateSnapshot(
                    p.id(),
                    p.selection
                            .getSelectedUnits()
                            .stream()
                            .toList()
            ));
        }

        var ul = new ArrayList<UnitStateSnapshot>();

        for (BaseUnit unit : unitStorage.values()) {
            if (unit instanceof DummyUnit du) {
                ul.add(new DummyUnitStateSnapshot(du.getId(), getUnitOwnerId(du), du.getName(), du.getClicks()));
            }
        }

        return new MatchStateSnapshot(tl, pll, sl, ul, getWinnerId(), meta.getTicks(), meta.getTickedTime(), isOver);
    }

    private Integer getUnitOwnerId(BaseUnit bu) {
        var owner = bu.getOwner();

        if (owner.isPresent()) {
            return owner.get().id();
        }

        return null;
    }

    private Integer getWinnerId() {
        if (definedWinner == null) {
            return null;
        }

        return definedWinner.id();
    }

    private void applyInput() {
        int processedActions = 0;
        while (!inputQueue.isEmpty() && processedActions < MAX_ACTIONS_PER_TICK) {
            dispatchAction(inputQueue.poll());
            processedActions++;
        }
    }

    private void dispatchAction(BaseAction action) {
        switch (action.type()) {
            case CLICK -> applyClickAction((SharedCommandAction) action);
            case LEAVE -> applyLeaveAction((SimpleGlobalAction) action);
            case SELECT -> applySelectAction((SelectAction) action);
            case SURRENDER -> applySurrenderAction((SimpleGlobalAction) action);
        }
    }

    private void applyClickAction(SharedCommandAction action) {
        if (!playerStorage.containsKey(action.playerId())) {
            return;
        }

        for (var unitId : playerStorage.get(action.playerId()).selection.getSelectedUnits()) {
            var unit = unitStorage.get(unitId);
            if (unit instanceof DummyUnit) {
                ((DummyUnit) unit).click();
            }
        }
    }

    private void applyLeaveAction(SimpleGlobalAction action) {
        if (playerStorage.containsKey(action.playerId())) {
            playerStorage.get(action.playerId()).leave();
            tryToDefineWinner();
            tryToEndMatch();
        }
    }

    private void applySelectAction(SelectAction action) {
        if (playerStorage.containsKey(action.playerId())) {
            var player = playerStorage.get(action.playerId());

            if (canSelect(player, action.getSelectedUnits())) {
                player.selection.setSelectedUnits(action.getSelectedUnits());
            }
        }
    }

    private boolean canSelect(Player player, Set<Integer> selection) {
        for (var unitId : selection) {
            if (!unitStorage.containsKey(unitId)) {
                return false;
            }

            Optional<Player> owner = unitStorage.get(unitId).getOwner();
            if (owner.isEmpty()) {
                return false;
            }

            if (owner.get().id() != player.id()) {
                return false;
            }
        }

        return true;
    }

    private void applySurrenderAction(SimpleGlobalAction action) {
        if (playerStorage.containsKey(action.playerId())) {
            playerStorage.get(action.playerId()).surrender();
            tryToDefineWinner();
        }
    }

    private void tryToDefineWinner() {
        Set<Integer> activeTeams = new HashSet<>();
        for (var player : playerStorage.values()) {
            if (player.status() == Player.Status.ACTIVE) {
                activeTeams.add(player.teamId());
            }
        }

        if (activeTeams.isEmpty() && definedWinner == null) {
            throw new IllegalStateException("At least one team should be active.");
        }

        if (activeTeams.size() == 1) {
            var winningTeamId = activeTeams.stream()
                    .findFirst()
                    .get();
            definedWinner = teamStorage.getOrDefault(winningTeamId, null);
        }
    }

    private void tryToEndMatch() {
        boolean shouldEnd = true;
        for (var player : playerStorage.values()) {
            if (player.status() != Player.Status.LEFT) {
                shouldEnd = false;
                break;
            }
        }

        isOver = shouldEnd;
    }

    Map<Integer, Player> getPlayerStorage_Test() {
        return playerStorage;
    }

    Map<Integer, BaseUnit> getUnitStorage_Test() {
        return unitStorage;
    }
}
