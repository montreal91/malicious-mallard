package org.example;

import java.util.Objects;

/**
 * Represents a player in the game with associated attributes and lifecycle state.
 */
public final class Player {
    private final int id;
    private final String name;
    private final int teamId;

    /**
     * The current status of the player in the match.
     */
    private Status status;

    /**
     * The current selection of units controlled by the player.
     */
    final Selection selection = new Selection();

    /**
     * Constructs a new Player with the specified attributes.
     *
     * @param id the unique identifier for the player, must be positive
     * @param name the name of the player, cannot be null or blank
     * @param teamId the team ID for the player
     */
    public Player(int id, String name, int teamId) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "Player name cannot be null");
        this.teamId = teamId;
        this.status = Status.ACTIVE;
    }

    public int id() {
        return id;
    }

    public String name() {
        return name;
    }

    public Status status() {
        return status;
    }

    public int teamId() {
        return teamId;
    }

    public void surrender() {
        status = Status.DEFEATED;
    }

    public void leave() {
        status = Status.LEFT;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Player) obj;
        return this.id == that.id &&
                Objects.equals(this.name, that.name) &&
                this.teamId == that.teamId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, teamId);
    }

    @Override
    public String toString() {
        return "Player[" +
                "id=" + id + ", " +
                "name=" + name + ", " +
                "teamId=" + teamId + ']';
    }

    /**
     * Represents the possible states a player can be in during a match.
     */
    public enum Status {
        ACTIVE, DEFEATED, LEFT
    }
}
