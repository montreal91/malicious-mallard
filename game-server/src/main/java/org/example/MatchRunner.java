package org.example;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

public class MatchRunner implements Runnable {
    private final AtomicBoolean isRunning = new AtomicBoolean(true);
    private final long dtMs;
    private final Map<String, Match> currentMatches = new LinkedHashMap<>();

    private final Queue<Match> newMatchesQueue;
    private final Queue<MatchResult> matchResultQueue;

    public MatchRunner(
            long dtMs,
            Queue<Match> newMatchesQueue,
            Queue<MatchResult> matchResultQueue
    ) {
        this.dtMs = dtMs;
        this.newMatchesQueue = Objects.requireNonNull(newMatchesQueue, "New matches queue cannot be null");
        this.matchResultQueue = Objects.requireNonNull(matchResultQueue, "Match result queue cannot be null");

    }

    @Override
    public void run() {
        long nextUpdateAt = System.currentTimeMillis();

        while (isRunning.get()) {
            nextUpdateAt += dtMs;
            updateMatches();
            removeFinishedMatches();
            addNewMatches();
            chillUntil(nextUpdateAt);
        }

        // Actually, doesn't make sense atm, because finished matches are removed from current matches.
        // Well, we'll collect match results.
        for (var match : currentMatches.values()) {
            System.out.println(match.getId());
            match.getMeta().printDrift_Test();
            System.out.println();
        }
    }

    public void stop() {
        isRunning.set(false);
    }

    private void chillUntil(long target) {
        try {
            long timeToChill = target - System.currentTimeMillis();
            if (timeToChill > 0) {
                Thread.sleep(timeToChill);
            } else {
                System.out.println("Slow update. " + timeToChill);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupt status
            System.err.println("MatchRunner interrupted");
            // Do better logging later
        }
    }

    private void addNewMatches() {
        while (!newMatchesQueue.isEmpty()) {
            var match = newMatchesQueue.poll();
            match.start();
            currentMatches.put(match.getId(), match);
        }
    }

    private void updateMatches() {
        for (Match match : currentMatches.values()) {
            match.update(dtMs);
        }
    }

    private void removeFinishedMatches() {
        var iterator = currentMatches.entrySet().iterator();

        while (iterator.hasNext()) {
            var entry = iterator.next();
            Match match = entry.getValue();

            if (match.isOver()) {
                match.end();
                match.getResult().ifPresent(matchResultQueue::add);
                iterator.remove();
            }
        }
    }
}
