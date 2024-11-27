package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MatchTest {

    private Match match;
    private Queue<BaseAction> inputQueue;
    private Queue<MatchStateSnapshot> outputQueue;

    @BeforeEach
    void setup() {
        inputQueue = new ConcurrentLinkedQueue<>();
        outputQueue = new ConcurrentLinkedQueue<>();

        var team1 = new Team(1);
        var team2 = new Team(2);
        var player1 = new Player(1, "Player1", 1);
        var player2 = new Player(2, "Player2", 2);
        var unit1 = new DummyUnit(1, player1, "Unit1");
        var unit2 = new DummyUnit(2, player2, "Unit2");

        var initialState = new InitialState(
                List.of(team1, team2),
                List.of(player1, player2),
                List.of(unit1, unit2)
        );

        match = new Match("TestMatch", initialState, inputQueue, outputQueue);
        match.start();
    }

    @Test
    void testSnapshotGenerationAfterEachTick() {
        // Arrange
        inputQueue.add(new SelectAction(1, Set.of(1))); // Player1 selects Unit1
        inputQueue.add(new SharedCommandAction(BaseAction.Type.CLICK, 1)); // Player1 clicks

        // Act
        match.update(40);

        // Assert
        assertFalse(outputQueue.isEmpty(), "The output queue should contain a state snapshot.");
        MatchStateSnapshot snapshot = outputQueue.poll();
        assertNotNull(snapshot, "The state snapshot should not be null.");
        assertEquals(1, snapshot.ticks(), "The snapshot should reflect the current tick count.");
        assertFalse(snapshot.isOver(), "The match should not be over in the snapshot.");
        assertEquals(2, snapshot.units().size(), "Snapshot should contain all unit states.");
    }

    @Test
    void testSnapshotAfterPlayerLeaves() {
        // Arrange
        inputQueue.add(new SimpleGlobalAction(BaseAction.Type.LEAVE, 1)); // Player1 leaves

        // Act
        match.update(40);

        // Assert
        MatchStateSnapshot snapshot = outputQueue.poll();
        assertNotNull(snapshot, "The state snapshot should not be null.");
        assertTrue(snapshot.players().stream()
                        .anyMatch(ps -> ps.status().equals("LEFT") && ps.id() == 1),
                "The snapshot should correctly record Player1's status as LEFT.");
    }

    @Test
    void testTryToDefineWinnerSingleTeamRemaining() {
        // Arrange
        inputQueue.add(new SimpleGlobalAction(BaseAction.Type.SURRENDER, 2)); // Player2 surrenders

        // Act
        match.update(40);

        // Assert
        assertTrue(match.getResult().isPresent());
        Integer winnerId = match.getResult().get().winner().id();
        assertNotNull(winnerId, "The match should have a winner.");
        assertEquals(1, winnerId, "Team1 should be declared the winner.");
    }

    @Test
    void testTryToEndMatchWhenAllPlayersLeave() {
        // Arrange
        inputQueue.add(new SimpleGlobalAction(BaseAction.Type.LEAVE, 1)); // Player1 leaves
        inputQueue.add(new SimpleGlobalAction(BaseAction.Type.LEAVE, 2)); // Player2 leaves

        // Act
        match.update(40);

        // Assert
        assertTrue(match.isOver(), "The match should end when all players leave.");
    }

    @Test
    void testTryToEndMatchWithRemainingPlayers() {
        // Arrange
        inputQueue.add(new SimpleGlobalAction(BaseAction.Type.LEAVE, 1)); // Player1 leaves

        // Act
        match.update(40);

        // Assert
        assertFalse(match.isOver(), "The match should not end if at least one player is active.");
    }

    @Test
    void testInteractionBetweenWinnerDefinitionAndEndMatch() {
        // Arrange
        inputQueue.add(new SimpleGlobalAction(BaseAction.Type.SURRENDER, 2)); // Player2 surrenders
        inputQueue.add(new SimpleGlobalAction(BaseAction.Type.LEAVE, 1)); // Player1 leaves

        // Act
        match.update(40);

        // Assert
        assertFalse(match.isOver(), "The match should end when all players are either surrendered or left.");
        assertTrue(match.getResult().isPresent(), "The match should produce a result.");
        assertEquals(1, match.getResult().get().winner().id(), "Team1 should be declared the winner.");

        inputQueue.add(new SimpleGlobalAction(BaseAction.Type.LEAVE, 2));
        match.update(40);
        assertTrue(match.isOver());
    }

    @Test
    void testClickActionWithSequentialFlow() {
        // Arrange
        inputQueue.add(new SharedCommandAction(BaseAction.Type.CLICK, 1)); // Player1 clicks

        // Act - Without selection
        match.update(40);

        // Assert - Player1's unit should not be clicked
        DummyUnit unit1 = (DummyUnit) match.getUnitStorage_Test().get(1);
        assertNotNull(unit1, "Unit1 should exist in the match.");
        assertEquals(0, unit1.getClicks(), "Unit1 should not be clicked as no units were selected.");

        // Arrange - Select Player1's unit and click again
        inputQueue.add(new SelectAction(1, Set.of(1))); // Player1 selects Unit1
        inputQueue.add(new SharedCommandAction(BaseAction.Type.CLICK, 1)); // Player1 clicks
        match.update(40);

        // Assert - Player1's unit should now be clicked
        assertEquals(1, unit1.getClicks(), "Unit1 should have been clicked once.");

        // Assert - Player2's unit should remain untouched
        DummyUnit unit2 = (DummyUnit) match.getUnitStorage_Test().get(2);
        assertNotNull(unit2, "Unit2 should exist in the match.");
        assertEquals(0, unit2.getClicks(), "Unit2 should not be clicked by Player1.");
    }

}
