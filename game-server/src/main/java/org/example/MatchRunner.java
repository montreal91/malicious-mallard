package org.example;

import java.util.concurrent.atomic.AtomicBoolean;

public class MatchRunner implements Runnable {
    private final AtomicBoolean isRunning = new AtomicBoolean(true);
    private final long dtMs;
    private final Match match;

    public MatchRunner(long dtMs, Match match) {
        this.dtMs = dtMs;
        this.match = match;
    }

    @Override
    public void run() {
        System.out.println("Match " + match.getId() + " is started.");
        long nextUpdateAt = System.currentTimeMillis();

        while (isRunning.get()) {
            nextUpdateAt += dtMs;
            match.update(dtMs);
            chillUntil(nextUpdateAt);
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
                System.out.println("Match " + match.getId() + " Slow update. " + timeToChill);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
