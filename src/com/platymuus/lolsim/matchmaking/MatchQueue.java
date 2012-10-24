package com.platymuus.lolsim.matchmaking;

import com.platymuus.lolsim.Simulation;
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
     * The simulation this MatchQueue is a part of.
     */
    private final Simulation sim;

    /**
     * The identifier (e.g. "normal5") for this queue.
     */
    private final String name;

    /**
     * The team size this queue uses.
     */
    private final int teamSize;

    /**
     * Initialize a new match queue.
     * @param name The short name of this queue, e.g. "normal5".
     * @param teamSize The size of teams in this queue (usually 3 or 5).
     * @param popularity How popular in general this queue is.
     */
    public MatchQueue(Simulation sim, String name, int teamSize, int popularity) {
        this.sim = sim;
        this.name = name;
        this.teamSize = teamSize;
    }

    /**
     * Get the name of this queue.
     * @return The queue's short id.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the simulation this queue belongs to.
     * @return The Simulation.
     */
    public Simulation getSimulation() {
        return sim;
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
        Match match = new Match(this, teamSize);
        match.add(summoners);
        matches.add(match);
    }

    /**
     * Tick each match in the queue.
     */
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

    @Override
    public String toString() {
        return "MatchQueue{" +
                "name='" + name + '\'' +
                ", teamSize=" + teamSize +
                ", matches=" + matches.size() +
                ", completedMatches=" + completedMatches.size() +
                '}';
    }
}
