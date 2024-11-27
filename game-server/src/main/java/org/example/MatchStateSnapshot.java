package org.example;

import java.util.List;
import java.util.Objects;

/**
 * Represents a snapshot of the current game state at a specific point in time.
 * This snapshot may include players, units, teams, and other relevant state information.
 */
public record MatchStateSnapshot(
        List<TeamStateSnapshot> teams,
        List<PlayerStateSnapshot> players,
        List<SelectionStateSnapshot> selections,
        List<UnitStateSnapshot> units,
        Integer winnerId,
        long ticks,
        long timeMs,
        boolean isOver
) {
    public MatchStateSnapshot(
            List<TeamStateSnapshot> teams,
            List<PlayerStateSnapshot> players,
            List<SelectionStateSnapshot> selections,
            List<UnitStateSnapshot> units,
            Integer winnerId,
            long ticks,
            long timeMs,
            boolean isOver
    ) {
        this.teams = List.copyOf(Objects.requireNonNull(teams));
        this.players = List.copyOf(Objects.requireNonNull(players));
        this.selections = List.copyOf(Objects.requireNonNull(selections));
        this.units = List.copyOf(Objects.requireNonNull(units));
        this.winnerId = winnerId;
        this.ticks = ticks;
        this.timeMs = timeMs;
        this.isOver = isOver;
    }
}
