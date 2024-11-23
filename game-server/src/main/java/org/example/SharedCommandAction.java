package org.example;

/**
 * Represents a shared command action performed by a player.
 * Examples of shared commands: move, attack-move, patrol, etc.
 * This class is intended to be used within a single-threaded context.
 */
public class SharedCommandAction extends BaseAction {
    public SharedCommandAction(Type type, int playerId) {
        super(type, playerId);
    }
}
