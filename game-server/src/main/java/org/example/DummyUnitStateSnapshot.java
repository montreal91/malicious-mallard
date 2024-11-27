package org.example;

public record DummyUnitStateSnapshot(
        int id,
        Integer ownerId,
        String name,
        int clicks
) implements UnitStateSnapshot {}
