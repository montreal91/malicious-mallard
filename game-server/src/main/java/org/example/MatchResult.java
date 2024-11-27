package org.example;

import java.util.Objects;

/**
 * Represents the result of a match.
 *
 * @param winner the winning team, or null if the match is tied
 * @param meta the metadata for the match
 */
public record MatchResult(Team winner, Meta meta) {
    public MatchResult(Team winner, Meta meta) {
        this.winner = winner;
        this.meta = Objects.requireNonNull(meta, "Meta cannot be null");
    }
}
