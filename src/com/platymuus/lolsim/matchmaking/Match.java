package com.platymuus.lolsim.matchmaking;

import com.platymuus.lolsim.players.Summoner;

import java.util.ArrayList;

/**
 * Represents an in-progress or complete match between players in a queue.
 */
public class Match {

    private final ArrayList<Summoner> players = new ArrayList<Summoner>();
    
    private final int size;
    
    private int totalElo = 0;
    
    private int range = 0;
    
    private int time = 0;

    public Match(int size) {
        this.size = size;
    }
    
    public boolean canAdd(int number, int elo) {
        return number <= size - players.size() && elo > avgElo - range && elo < avgElo + range;
    }
    
    public void add(Summoner... summoners) {
        for (Summoner guy : summoners) {
            players.add(guy);
            totalElo +=
        }
    }
    
    public boolean isComplete() {
        return players.size() == size;
    }
    
    public void tick() {
        
    }

}
