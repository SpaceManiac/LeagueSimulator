package com.platymuus.lolsim.matchmaking;

import com.platymuus.lolsim.players.Summoner;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * Represents a queue in which players can enter to be matched with others.
 */
public class MatchQueue {

    /**
     * The deque of completed matches.
     */
    private final ArrayDeque<Match> completedMatches = new ArrayDeque<Match>();

    /**
     * Matches that have not been completed.
     */
    private final ArrayList<Match> matches = new ArrayList<Match>();

    /**
     * The name thing.
     */
    private final String name;

    public MatchQueue(String name, int teamSize, int popularity) {
        this.name = name;
    }

    /**
     * Enter a summoner or summoners into the matchmaking queue together.
     * @param summoners The Summoners to enter.
     */
    public void addPlayers(Summoner... summoners) {
        int elo = 0;
        for (Summoner guy : summoners) {
            elo += guy.getElo(name);
        }
        for (Match match : new ArrayList<Match>(matches)) {
            if (match.canAdd(summoners.length, elo / summoners.length)) {
                match.add(summoners);
                if (match.isComplete()) {
                    matches.remove(match);
                    completedMatches.add(match);
                }
                return;
            }
        }
        Match match = new Match(5);
        match.add(summoners);
    }

    public void tick() {
        for (Match match : matches) {
            match.tick();
        }
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
