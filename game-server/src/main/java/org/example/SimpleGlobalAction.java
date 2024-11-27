package org.example;

/**
 * Represents a global action that applies to the entire game state.
 * This class is designed for single-threaded use.
 */
public class SimpleGlobalAction extends BaseAction {
    public SimpleGlobalAction(Type type, int playerId) {
        super(type, playerId);
    }
}
