package org.example;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GameServer {
    private static final long MILLIS_IN_SECOND = 1000;
    private static final long MILLIS_IN_MINUTE = 60 * MILLIS_IN_SECOND;
    private static final long DELTA_TIME_MS = 40;
    private static final long MATCH_ADD_STEP = 1;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("This is a real-time game server.");

        var matchQueue1 = new ConcurrentLinkedQueue<Match>();
        var matchQueue2 = new ConcurrentLinkedQueue<Match>();
        var matchResultQueue = new ConcurrentLinkedQueue<MatchResult>();

        var mt1 = new MatchRunner(DELTA_TIME_MS, matchQueue1, matchResultQueue);
        var mt2 = new MatchRunner(DELTA_TIME_MS, matchQueue2, matchResultQueue);

        var match1ActualThread = new Thread(mt1);
        var match2ActualThread = new Thread(mt2);

        match1ActualThread.start();
        match2ActualThread.start();

        var iq1 = new ConcurrentLinkedQueue<BaseAction>();
        var iq2 = new ConcurrentLinkedQueue<BaseAction>();
        var iq3 = new ConcurrentLinkedQueue<BaseAction>();

        var outputQueue = new ConcurrentLinkedQueue<MatchStateSnapshot>();

        matchQueue1.add(new Match("Zerg", makeDummyInitialState(), iq1, outputQueue));
        Thread.sleep(MILLIS_IN_MINUTE * MATCH_ADD_STEP);

        matchQueue1.add(new Match("Toss", makeDummyInitialState(), iq2, outputQueue));
        Thread.sleep(MILLIS_IN_MINUTE * MATCH_ADD_STEP);

        matchQueue1.add(new Match("Terran", makeDummyInitialState(), iq3, outputQueue));
        Thread.sleep(MILLIS_IN_MINUTE * MATCH_ADD_STEP);

        iq1.add(makeLeaveAction(1));
        iq2.add(makeLeaveAction(2));
        iq3.add(makeLeaveAction(1));
        Thread.sleep(MILLIS_IN_SECOND * 10);

        iq1.add(makeLeaveAction(2));
        iq2.add(makeLeaveAction(1));
        iq3.add(makeLeaveAction(2));
        Thread.sleep(MILLIS_IN_SECOND * 10);

        mt1.stop();
        mt2.stop();

        match1ActualThread.join();
        match2ActualThread.join();

        while (!matchResultQueue.isEmpty()) {
            var v = matchResultQueue.poll();
            v.meta().printDrift_Test();
        }
    }

    private static InitialState makeDummyInitialState() {
        var t1 = new Team(1);
        var t2 = new Team(2);
        var p1 = new Player(1, "LastWagon", 1);
        var p2 = new Player(2, "Kitsune", 2);
        var u1 = new DummyUnit(1, p1, "Dummy");
        var u2 = new DummyUnit(2, p2, "Dummy");

        return new InitialState(
                List.of(t1, t2),
                List.of(p1, p2),
                List.of(u1, u2)
        );
    }

    private static BaseAction makeLeaveAction(int playerId) {
        return new SimpleGlobalAction(BaseAction.Type.LEAVE, playerId);
    }
}
