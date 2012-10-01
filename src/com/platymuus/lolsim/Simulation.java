package com.platymuus.lolsim;

import com.platymuus.lolsim.matchmaking.Match;
import com.platymuus.lolsim.matchmaking.MatchQueue;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO: Javadoc for Simulation
 */
public class Simulation {

    /**
     * The available queues that players may enter.
     */
    private final HashMap<String, MatchQueue> queues = new HashMap<String, MatchQueue>();

    /**
     * The amount of time the simulation has been running.
     */
    private long timeElapsed = 0;

    /**
     * The subsecond amount of time the simulation has been running.
     */
    private double subseconds = 0;

    /**
     * Tick the simulation for the given length of simulated time.
     * @param time The time in seconds to tick for.
     */
    public void update(double time) {
        subseconds += time;
        while (subseconds >= 1) {
            tick();
            subseconds -= 1;
            timeElapsed += 1;
        }
    }

    private void tick() {
        for (Map.Entry<String, MatchQueue> entry : queues.entrySet()) {
            Match match = null;
            while ((match = entry.getValue().getMatch()) != null) {

            }
        }
    }

}
