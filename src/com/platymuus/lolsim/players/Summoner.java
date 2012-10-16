package com.platymuus.lolsim.players;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents persistent data about an individual Summoner.
 */
public class Summoner {

    /**
     * The summoner's name
     */
    private String name;

    /**
     * The players prefered class, ex. Tank, Passive Suport, Jungle ect.
     */
    private String affinity;

    /**
     * Summoner Level
     */
    private int level;

    /**
     * The player's general skill at video games
     */
    private int skill;

    /**
     * The skill a player gets as he learns how the game works.
     */
    private int learnedSkill;

    /**
     * Map from champion name and number played to how good a player is at said champion.
     */
    private HashMap<String, Integer> champs;

    /**
     * The amount a play is online between never(0) and 24/7(1)
     */
    private double activity = 0.2;

    /**
     * The map of per-queue information on this summoner.
     */
    private final HashMap<String, QueueInfo> queueInfo = new HashMap<String, QueueInfo>();

    /**
     * Will calculate the individual players score
     *
     * @return players score
     */
    public int score() {
        return 5;
    }

    /**
     * Get the Elo rating for this summoner in a given queue.
     *
     * @param queue The queue id.
     * @return The Elo rating.
     */
    public int getElo(String queue) {
        return queueInfo(queue).elo;
    }

    public void setElo(String queue, int elo) {
        queueInfo(queue).elo = elo;
    }

    public double getActivity() {
        return activity;
    }

    private QueueInfo queueInfo(String queue) {
        if (!queueInfo.containsKey(queue)) {
            queueInfo.put(queue, new QueueInfo());
        }
        return queueInfo.get(queue);
    }

    private class QueueInfo {
        public int elo = 1200;
        public int won = 0;
        public int lost = 0;
        public int weight = 0;
    }

}
