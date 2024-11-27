package org.example;

/**
 * Tracks metadata about a match, including timing, ticks, and drift information.
 * This class is primarily used for debugging and performance analysis of match updates.
 * This class is designed to be used only within a single thread.
 */
public class Meta {
    /**
     * The timestamp (in milliseconds) when the match was started.
     */
    private long startTime = -1;

    /**
     * The timestamp (in milliseconds) when the match was last updated or ended.
     */
    private long lastUpdateAt = -1;

    /**
     * The total number of update ticks performed for the match.
     * Each tick represents a single logical update cycle in the match.
     */
    private long ticks = 0;

    /**
     * The total time (in milliseconds) that has elapsed during ticks.
     * This value is incremented by the delta time (dt) passed to each tick.
     */
    private long tickedTime = 0;

    /**
     * The cumulative time (in milliseconds) spent performing match updates.
     * This is the total time taken by the match update logic across all ticks.
     */
    private long accumulatedUpdateTime = 0;

    /**
     * The cumulative drift (in milliseconds) between the expected time
     * and actual elapsed time between updates.
     * Drift occurs due to delays or timing inconsistencies in the update cycle.
     */
    private long accumulatedDrift = 0;

    /**
     * Records the start time of the match.
     * This should be called at the beginning of a match lifecycle.
     */
    public void start() {
        startTime = System.currentTimeMillis();
    }

    /**
     * Records the end time of the match.
     * This should be called when the match concludes.
     */
    public void end() {
        if (startTime == -1) {
            throw new IllegalStateException("Meta tracking has not been started");
        }
        lastUpdateAt = System.currentTimeMillis();
    }

    /**
     * Updates tick-related metadata.
     * This should be called on every tick to track the logical
     * time progression of the match.
     *
     * @param dt the delta time (in milliseconds) to increment the ticked time
     */
    public void tick(long dt) {
        ticks++;
        tickedTime += dt;
    }

    public long getTickedTime() {
        return tickedTime;
    }

    public long getTicks() {
        return ticks;
    }

    /**
     * Adds drift to the accumulated drift value.
     * Drift represents the difference between the expected and actual elapsed time
     * between updates.
     *
     * @param drift the amount of drift (in milliseconds) to add
     */
    public void addDrift(long drift) {
        accumulatedDrift += drift;
    }

    public void addUpdateTime(long ut) {
        accumulatedUpdateTime += ut;
    }

    /**
     * Prints a detailed drift and performance analysis of the match lifecycle.
     * This is primarily used for debugging and analyzing server timing issues.
     */
    public void printDrift_Test() {
        long actualDurationMs = lastUpdateAt - startTime;
        long drift = Math.abs(tickedTime - actualDurationMs);

        System.out.println("Ticked Duration: [" + tickedTime + "] Actual Duration: [" + actualDurationMs + "]");
        System.out.println("Time drift: [" + drift + "]");
        System.out.println("Ticks: " + ticks);
        System.out.println("Tick Length: " + (double) tickedTime / ticks + " ms.");
        System.out.println("Drift per tick: " + (double) drift / ticks);
        System.out.println("Average accumulated drift per tick: " + (double) accumulatedDrift / ticks);
        System.out.println("Accumulated update time: " + accumulatedUpdateTime);
        System.out.println("Average update time: " + (double) accumulatedUpdateTime / ticks);
    }
}
