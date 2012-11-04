package com.platymuus.lolsim;

import com.platymuus.lolsim.matchmaking.Match;
import com.platymuus.lolsim.matchmaking.Team;
import com.platymuus.lolsim.players.Summoner;

import java.util.HashMap;

/**
 * To pull necessary information from the match class, determine winner, and give rewards to necessary players
 */
public class Game {

    /**
     * The Simulation the game is a part of.
     */
    private final Simulation sim;

    /**
     * The Match that generated this game's players.
     */
    private final Match match;

    /**
     * A semi-unique identifier for this game.
     */
    private final String id;

    /**
     * The amount of time this game is set to last.
     */
    private int totalTime;

    /**
     * The amount of time in seconds this game has lasted.
     */
    private int timeElapsed;

    /**
     * The cached winning team.
     */
    private Team winner;

    /**
     * The number of digits in game IDs.
     */
    private final static int DIGITS = 9;

    /**
     * Construct a new game.
     *
     * @param sim   The Simulation this match belongs to.
     * @param match The Match that generated this game's players.
     */
    public Game(Simulation sim, Match match) {
        this.sim = sim;
        this.match = match;

        id = "na" + (long) (Math.pow(10, DIGITS) - 1 + Math.random() * 9 * Math.pow(10, DIGITS));
        totalTime = 20 * 60 + sim.getRandom().nextInt(30 * 60);
        timeElapsed = 0;
    }

    /**
     * Tick this game for one second.
     */
    public void tick() {
        timeElapsed += 1;
    }

    /**
     * Determine of the game has ended by now.
     *
     * @return Whether the game has ended.
     */
    public boolean hasEnded() {
        return timeElapsed > totalTime;
    }

    /**
     * Get this game's text id.
     *
     * @return The id.
     */
    public String getId() {
        return id;
    }

    /**
     * Return the match this game is composed of.
     *
     * @return The Match.
     */
    public Match getMatch() {
        return match;
    }

    /**
     * Calculate the winning team of this game.
     *
     * @return The winning Team. Not guaranteed to be the same between calls.
     */
    public Team getWinner() {
        if (winner != null) return winner;

        int totalScore = 0;
        HashMap<Team, Integer> scores = new HashMap<Team, Integer>();
        for (Team team : match.getTeams()) {
            int score = 0;
            for (Summoner guy : match.getPlayers(team)) {
                score += guy.score();
            }
            scores.put(team, score);
            totalScore += score;
        }

        int randScore = sim.getRandom().nextInt(totalScore);
        totalScore = 0;
        for (Team team : match.getTeams()) {
            totalScore += scores.get(team);
            if (randScore <= totalScore) {
                winner = team;
                return team;
            }
        }

        // Hopefully shouldn't get here
        return null;
    }

    @Override
    public String toString() {
        return "Game{" +
                "id='" + id + '\'' +
                ", match=" + match +
                ", totalTime=" + totalTime +
                ", timeElapsed=" + timeElapsed +
                '}';
    }
}
