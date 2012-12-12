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
            return o2.getWon(queue) - o1.getWon(queue);
        }
    }

    public static class MostElo extends PerQueue {
        public MostElo(String queue) {
            super(queue);
        }

        public int compare(Summoner o1, Summoner o2) {
            return o2.getElo(queue) - o1.getElo(queue);
        }
    }

    public static class MostEloHideNoobs extends PerQueue {
        public MostEloHideNoobs(String queue) {
            super(queue);
        }

        public int compare(Summoner o1, Summoner o2) {
            if (noob(o2) && !noob(o1)) return -1;
            if (noob(o1) && !noob(o2)) return 1;
            return o2.getElo(queue) - o1.getElo(queue);
        }

        private boolean noob(Summoner o2) {
            return o2.getWon(queue) + o2.getLost(queue) == 0;
        }
    }

}
