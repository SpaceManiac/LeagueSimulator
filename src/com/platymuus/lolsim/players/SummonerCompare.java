package com.platymuus.lolsim.players;

import java.util.Comparator;

/**
 * Container class for summoner comparators.
 */
public final class SummonerCompare {

    private SummonerCompare() {
    }

    private static abstract class PerQueue implements Comparator<Summoner> {
        protected final String queue;

        protected PerQueue(String queue) {
            this.queue = queue;
        }
    }

    public static class MostWins extends PerQueue {
        public MostWins(String queue) {
            super(queue);
        }

        public int compare(Summoner o1, Summoner o2) {
            return o2.getWon("normal5") - o1.getWon("normal5");
        }
    }

    public static class MostElo extends PerQueue {
        public MostElo(String queue) {
            super(queue);
        }

        public int compare(Summoner o1, Summoner o2) {
            return o2.getElo("normal5") - o1.getElo("normal5");
        }
    }

}
