package org.example;

import java.util.Collections;
import java.util.Set;

public class Selection {
    private Set<Integer> selectedUnits = Collections.emptySet();

    public void setSelectedUnits(Set<Integer> selectedUnits) {
        this.selectedUnits = selectedUnits;
    }

    public Set<Integer> getSelectedUnits() {
        return selectedUnits;
    }
}
