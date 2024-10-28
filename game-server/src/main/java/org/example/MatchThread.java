package org.example;

import java.util.concurrent.atomic.AtomicBoolean;

public class MatchThread implements Runnable {
    private final AtomicBoolean isRunning = new AtomicBoolean(true);
    private final long dtMs;
    private final Match match;
    private long ticks = 0;

    public MatchThread(long dtMs, Match match) {
        this.dtMs = dtMs;
        this.match = match;
    }

    @Override
    public void run() {
        System.out.println("Match " + match.getGuid() + " is started.");
        long startTime = System.currentTimeMillis();
        long target;

        while (isRunning.get()) {
            ticks++;
            target = startTime + dtMs * ticks;
            match.update(dtMs);

            try {
                Thread.sleep(Math.max(10, target - System.currentTimeMillis()));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void stop() {
        isRunning.set(false);
    }
}
