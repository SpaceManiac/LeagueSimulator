package com.platymuus.lolsim;

import com.platymuus.lolsim.matchmaking.Match;
import com.platymuus.lolsim.matchmaking.Team;
import com.platymuus.lolsim.players.Summoner;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * To pull necessary information from the match class, determine winner, and give rewards to necessary players
 */
public class Game {

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
     * Construct a new game.
     * @param match The Match that generated this game's players.
     */
    public Game(Match match) {
        this.match = match;
        
        id = "na" + (int)(Math.random() * Integer.MAX_VALUE);
        
        totalTime = 10;
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
     * @return Whether the game has ended.
     */
    public boolean hasEnded() {
        return timeElapsed > totalTime;
    }

    /**
     * Calculate the winning team of this game.
     * @return The winning Team. Not guaranteed to be the same between calls.
     */
    public Team getWinner() {
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
        
        int randScore = (int)(Math.random() * totalScore);
        totalScore = 0;
        for (Team team : match.getTeams()) {
            totalScore += scores.get(team);
            if (randScore <= totalScore) {
                return team;
            }
        }

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
