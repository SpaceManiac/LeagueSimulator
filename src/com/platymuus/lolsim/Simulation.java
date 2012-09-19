package com.platymuus.lolsim;

import com.platymuus.lolsim.matchmaking.MatchQueue;

import java.util.HashMap;

/**
 * TODO: Javadoc for Simulation
 */
public class Simulation {

    /**
     * The available queues that players may enter.
     */
    private final HashMap<String, MatchQueue> queues = new HashMap<String, MatchQueue>();

    /**
     * The total amount of simulated time the simulation has been running.
     */
    private double timeElapsed = 0;

    /**
     * Tick the simulation for the given length of simulated time.
     * @param time The time in seconds to tick for.
     */
    public void update(double time) {

        timeElapsed += time;
    }

}
