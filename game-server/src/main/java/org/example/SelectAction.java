package org.example;

import java.util.Objects;
import java.util.Set;

/**
 * Represents an action where a player selects units in the game.
 * This class is designed to be used only within a single thread.
 */
public class SelectAction extends BaseAction {
    /**
     * The set of unit IDs selected by the player.
     */
    private final Set<Integer> selectedUnits;

    /**
     * Constructs a new SelectAction for the given player and selected units.
     *
     * @param playerId the ID of the player performing the action
     * @param selectedUnits the set of unit IDs selected by the player; cannot be null
     * @throws NullPointerException if selections is null
     */
    public SelectAction(int playerId, Set<Integer> selectedUnits) {
        super(Type.SELECT, playerId);
        this.selectedUnits = Set.copyOf(Objects.requireNonNull(
                selectedUnits, "Selected units cannot be null"
        ));
    }

    /**
     * Returns the set of unit IDs selected by the player.
     *
     * @return an unmodifiable set of selected unit IDs
     */
    public Set<Integer> getSelectedUnits() {
        return selectedUnits;
    }
}
