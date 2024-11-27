package org.example;

import java.util.Optional;

public abstract class BaseUnit {
    private final int id;
    private final Player owner;
    private final String name;

    protected BaseUnit(int id, Player owner, String name) {
        this.id = id;
        this.owner = owner; // This value can be nullable. Null value means that this unit is neutral.
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public Optional<Player> getOwner() {
        return Optional.ofNullable(owner);
    }

    @Override
    public String toString() {
        return "BaseUnit{" +
                "id=" + id +
                ", owner=" + owner +
                ", name='" + name + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }
}
