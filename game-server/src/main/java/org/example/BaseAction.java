package org.example;

import java.util.Objects;

public abstract class BaseAction {
    private final Type type;
    private final int playerId;

    public BaseAction(Type type, int playerId) {
        this.type = Objects.requireNonNull(type, "Type cannot be null");
        this.playerId = playerId;
    }

    public Type type() {
        return type;
    }

    public int playerId() {
        return playerId;
    }

    @Override
    public String toString() {
        return "Action[" +
                "type=" + type + ", " +
                "playerId=" + playerId + ']';
    }

    public enum Type {
        CLICK, SELECT, SURRENDER, LEAVE
    }
}
