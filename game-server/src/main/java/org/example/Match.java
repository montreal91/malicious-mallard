package org.example;

public class Match {
    private final String id; // String is temporary, should be UUID
    private long timeMs = 0;
    private final long startTime;
    private long lastUpdateAt;
    private long ticks = 0;
    private long accumulatedUpdateTime = 0;
    private long accumulatedDrift = 0;

    public Match(String id) {
        this.id = id;

        startTime = System.currentTimeMillis();
        lastUpdateAt = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public void update(long dt) {
        long updateStart = System.currentTimeMillis();
        timeMs += dt;
        ticks++;
        long currUpdate = System.currentTimeMillis();
        long timeSinceLastUpdate = currUpdate - lastUpdateAt;
        accumulatedDrift += Math.abs(timeSinceLastUpdate - dt);

        lastUpdateAt = currUpdate;
        long updateEnd = System.currentTimeMillis();
        accumulatedUpdateTime += (updateEnd - updateStart);
    }

    public void printDrift_Test() {
        long actualDurationMs = lastUpdateAt - startTime;
        long drift = Math.abs(timeMs - actualDurationMs);

        System.out.println(id + " Ticked Duration: [" + timeMs + "] Actual Duration: [" + actualDurationMs + "]");
        System.out.println("Time drift: [" + Math.abs(timeMs - actualDurationMs) + "]");
        System.out.println("Ticks: " + ticks);
        System.out.println("Drift per tick: " + (double) drift / ticks);
        System.out.println("Average accumulated drift per tick: " + (double) accumulatedDrift / ticks);
        System.out.println("Accumulated update time: " + accumulatedUpdateTime);
        System.out.println("Average update time: " + (double) accumulatedUpdateTime / ticks);
    }
}
