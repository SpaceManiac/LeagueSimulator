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

    private final Match match;

    private final String id;
    
    private int totalTime;
    
    private int timeElapsed;

    public Game(Match match) {
        this.match = match;
        
        id = "na" + (int)(Math.random() * Integer.MAX_VALUE);
        
        totalTime = 10;
        timeElapsed = 0;
    }

    public void tick() {
        timeElapsed += 1;
    }

    public boolean hasEnded() {
        return timeElapsed > totalTime;
    }

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
