package com.platymuus.lolsim;

import java.util.Random;

/**
 * A Random provided with additional utility methods for the simulation.
 */
public class SimRandom extends Random {

    public SimRandom() {
    }

    public SimRandom(long seed) {
        super(seed);
    }

    /**
     * Randomly calculates whether an event should occur this tick based on the mean number of ticks until that event.
     * @param mean The mean time it should take for an event to occur, in ticks.
     * @return Whether the event should occur this tick.
     */
    public boolean nextTickChance(double mean) {
        return nextDouble() < tickChanceByTime(mean);
    }

    /**
     * Calculate the per-tick chance of an event based on the mean number of ticks until that event.
     * @param mean The mean time it should take for an event to occur, in seconds.
     * @return The random chance that the event should occur each tick.
     */
    private double tickChanceByTime(double mean) {
        // k = 0.5 = % chance that event will have occurred by the mean
        // k = 1 - (1 - p)^n
        // 1 - k = (1 - p)^n
        // n\(1-k) = 1 - p
        // n\(1-k) - 1 = -p
        // p = 1 - n root (1-k)
        return 1 - Math.pow(1 - 0.5, 1 / mean);
    }

}
