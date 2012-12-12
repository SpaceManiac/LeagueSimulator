package com.platymuus.lolsim.players;

import com.platymuus.lolsim.SimRandom;

import java.util.HashMap;

/**
 * Represents persistent data about an individual Summoner.
 */
public class Summoner {


    /**
     * The summoner's name.
     */
    private String name;

    /**
     * The players preffered class, ex. Tank, Passive Support, Jungle ect.
     */
    private String affinity;

    /**
     * Summoner Level.
     */
    private int level;

    /**
     * The player's general skill at video games.
     */
    private int skill;

    /**
     * The skill a player gets as he learns how the game works.
     */
    private int learnedSkill = 0;

    /**
     * Map from champion name and number played to how good a player is at said champion.
     */
    private final HashMap<String, Integer> champs = new HashMap<String, Integer>();

    /**
     * The amount a play is online between never(0) and 24/7(1).
     */
    private double activity;

    /**
     * The number of games played and learned from.
     */
    private int gamesPlayed;

    /**
     * The map of per-queue information on this summoner.
     */
    private final HashMap<String, QueueInfo> queueInfo = new HashMap<String, QueueInfo>();
    private int logoffs;
    private int logons;

    /**
     * Construct a new summoner.
     */
    public Summoner() {
        activity = calculateActivity();
        skill = 1 + (int) (Math.random() * 100);
        name = SimRandom.generateName();
    }

    /**
     * Get this Summoner's name.
     *
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Calculate the total individual skill score of this summoner.
     *
     * @return The score.
     */
    public int score() {
        return skill + learnedSkill + (int) (Math.random() * 100);
    }

    /**
     * Is used whevever a player wins or loses a game to determine how much they learned from that game
     */
    public void learn() {
        gamesPlayed += 1;
        learnedSkill += (int) (3 * Math.pow(Math.E, -.002 * Math.E * gamesPlayed));
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

    /**
     * Set the Elo rating for this summoner in a given queue.
     *
     * @param queue The queue id.
     * @param elo   The Elo rating.
     */
    public void setElo(String queue, int elo) {
        queueInfo(queue).elo = elo;
    }

    /**
     * Get the win count for this summoner in a given queue.
     *
     * @param queue The queue id.
     * @return The number of wins.
     */
    public int getWon(String queue) {
        return queueInfo(queue).won;
    }

    /**
     * Set the win count for this summoner in a given queue.
     *
     * @param queue The queue id.
     * @param won   The number of wins.
     */
    public void setWon(String queue, int won) {
        queueInfo(queue).won = won;
    }

    /**
     * Get the loss count for this summoner in a given queue.
     *
     * @param queue The queue id.
     * @return The number of losses.
     */
    public int getLost(String queue) {
        return queueInfo(queue).lost;
    }

    /**
     * Set the loss count for this summoner in a given queue.
     *
     * @param queue The queue id.
     * @param lost  The number of losses.
     */
    public void setLost(String queue, int lost) {
        queueInfo(queue).lost = lost;
    }

    /**
     * Add a logon to the summoner's statistics.
     */
    public void addLogon() {
        logons++;
    }

    /**
     * Add a logoff to the summoner's statistics.
     */
    public void addLogoff() {
        logoffs++;
    }

    /**
     * Returns a summoner's preference weight for a given queue.
     *
     * @param queue The queue id.
     * @return The preference weight.
     */
    public int getWeight(String queue) {
        return queueInfo(queue).weight;
    }

    /**
     * Set a summoner's preference weight for a given queue.
     *
     * @param queue  The queue id.
     * @param weight The preference weight.
     */
    public void setWeight(String queue, int weight) {
        queueInfo(queue).weight = weight;
    }

    /**
     * Return often this summoner is online.
     *
     * @return A value from 0.0 to 1.0 indicating how much of the time the summoner is online.
     */
    public double getActivity() {
        return activity;
    }

    /**
     * Calculates a players activity between 1 and 0
     *
     * @return activity
     */
    private double calculateActivity() {
        double random = Math.random();
        if (random < .05) {
            return Math.pow(Math.E, -60 * random);
        } else if (random < .95) {
            return Math.sin(Math.sqrt(random) * Math.PI / 180);
        } else {
            return (-(Math.pow(random - .8585779, 2) + .02));
        }
    }

    /**
     * Create or get a QueueInfo structure for a queue.
     *
     * @param queue The queue id.
     * @return The existing or created QueueInfo.
     */
    private QueueInfo queueInfo(String queue) {
        if (!queueInfo.containsKey(queue)) {
            queueInfo.put(queue, new QueueInfo());
        }
        return queueInfo.get(queue);
    }

    /**
     * A storage structure for per-queue data.
     */
    private class QueueInfo {
        public int elo = 1200;
        public int won = 0;
        public int lost = 0;
        public int weight = 1;
    }

}
