package com.platymuus.lolsim.matchmaking;

import com.platymuus.lolsim.players.Summoner;
import com.sun.jmx.remote.internal.ArrayQueue;

import java.util.ArrayDeque;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * Represents a queue in which players can enter to be matched with others.
 */
public class MatchQueue {

    /**
     *
     */
    private ArrayDeque<Match> completedMatches;
    
    public MatchQueue(int teamSize, int popularity) {
        
    }

    /**
     * Enter a summoner into the matchmaking queue.
     * @param summoner The Summoner to enter.
     */
    public void addPlayer(Summoner summoner) {
        
    }

    /**
     * Check for the existence of completed matches.
     * @return Whether getMatch() will return anything.
     */
    public boolean matchesFound() {
        // filter through matches assigning players and completing if needed
        return completedMatches.size() > 0;
    }

    /**
     * Gets the next completed match
     * @return A completed Match object.
     */
    public Match getMatch() {
        // just pop from completed match queue
        try {
            return completedMatches.pop();
        }
        catch (NoSuchElementException ex) {
            return null;
        }
    }
    
}
