package org.example;

import java.util.List;
import java.util.Objects;

public record SelectionStateSnapshot(int playerId, List<Integer> selectedUnits) {
    public SelectionStateSnapshot(int playerId, List<Integer> selectedUnits) {
        this.playerId = playerId;
        this.selectedUnits = Objects.requireNonNull(selectedUnits);
    }
}
