package org.example;

public class GameServer {
    private static final long MILLIS_IN_SECOND = 1000;
    private static final long MILLIS_IN_MINUTE = 60 * MILLIS_IN_SECOND;
    private static final long DELTA_TIME_MS = 40;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("This is a real-time game server.");

        var match1 = new Match("Zerg");
        var match2 = new Match("Toss");

        var mt1 = new MatchRunner(DELTA_TIME_MS, match1);
        var mt2 = new MatchRunner(DELTA_TIME_MS, match2);

        var match1ActualThread = new Thread(mt1);
        var match2ActualThread = new Thread(mt2);

        match1ActualThread.start();
        match2ActualThread.start();

        Thread.sleep(MILLIS_IN_MINUTE);

        mt1.stop();
        mt2.stop();

        match2ActualThread.join();
        match2ActualThread.join();

        System.out.println();
        match1.printDrift_Test();
        System.out.println();
        match2.printDrift_Test();
    }
}
