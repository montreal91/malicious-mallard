package org.example;

import java.util.Objects;

public record PlayerStateSnapshot(int id, int teamId, String name, String status) {
    public PlayerStateSnapshot(int id, int teamId, String name, String status) {
        this.id = id;
        this.teamId = teamId;
        this.name = Objects.requireNonNull(name);
        this.status = Objects.requireNonNull(status);
    }
}
