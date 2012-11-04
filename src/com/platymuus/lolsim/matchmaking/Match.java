package com.platymuus.lolsim.matchmaking;

import com.platymuus.lolsim.players.Summoner;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Set;

/**
 * Represents an in-progress or complete match between players in a queue.
 */
public class Match {

    /**
     * The map from teams to their contents.
     */
    private final EnumMap<Team, ArrayList<Summoner>> players = new EnumMap<Team, ArrayList<Summoner>>(Team.class);

    /**
     * The MatchQueue this Match was created by.
     */
    private final MatchQueue queue;

    /**
     * The desired size of each team.
     */
    private final int teamSize;

    /**
     * The current number of players across all teams.
     */
    private int size = 0;

    /**
     * The total Elo between all players currently matched.
     */
    private int totalElo = 0;

    /**
     * The range within the mean Elo a player must be to be matched.
     */
    private int range = 20;

    /**
     * How long the match has been pending.
     */
    private int time = 0;

    /**
     * Create a new Match.
     *
     * @param queue    The queue this Match belongs to.
     * @param teamSize The desired size of each team.
     */
    public Match(MatchQueue queue, int teamSize) {
        this.queue = queue;
        this.teamSize = teamSize;

        // Default teams
        for (Team team : Team.values()) {
            players.put(team, new ArrayList<Summoner>());
        }
    }

    /**
     * Get the queue this match was created by.
     *
     * @return The parent MatchQueue.
     */
    public MatchQueue getQueue() {
        return queue;
    }

    /**
     * Check if a given set of players can fit into the match.
     *
     * @param number The number of players to queue together.
     * @param elo    The average Elo of the players.
     * @return True if these players can fit into the match.
     */
    public boolean canAdd(int number, int elo) {
        return teamChoices(number).size() > 0 &&
                elo > totalElo / size - range &&
                elo < totalElo / size + range;
    }

    /**
     * Add players into the match. Should be checked with canAdd first.
     *
     * @param summoners The players to add to the match.
     */
    public void add(Summoner... summoners) {
        ArrayList<Team> choices = teamChoices(summoners.length);
        Team team = choices.get(0);

        for (Summoner guy : summoners) {
            players.get(team).add(guy);
            totalElo += guy.getElo(queue.getName());
        }
        totalElo += summoners.length * summoners.length; // Group elo bonus
        size += summoners.length;
    }

    /**
     * Check if the match is ready to be a game.
     *
     * @return True if all teams are full.
     */
    public boolean isComplete() {
        return teamChoices(1).size() == 0;
    }

    /**
     * Tick the match for one second.
     */
    public void tick() {
        ++time;
        if (time % 60 == 0) {
            range += 4;
        }
    }

    /**
     * Return the set of teams in this Match.
     *
     * @return The ser of teams.
     */
    public Set<Team> getTeams() {
        return players.keySet();
    }

    /**
     * Get the players for a given team.
     *
     * @param team The Team to get players for.
     * @return The list of players.
     */
    public ArrayList<Summoner> getPlayers(Team team) {
        return new ArrayList<Summoner>(players.get(team));
    }

    /**
     * Calculate which teams can fit the given number of players.
     *
     * @param length The number of players to fit.
     * @return The list of teams.
     */
    private ArrayList<Team> teamChoices(int length) {
        ArrayList<Team> list = new ArrayList<Team>();
        for (Team team : players.keySet()) {
            if (players.get(team).size() + length <= teamSize) {
                list.add(team);
            }
        }
        return list;
    }

    @Override
    public String toString() {
        return "Match{" +
                "queue='" + queue + '\'' +
                ", teamSize=" + teamSize +
                ", size=" + size +
                ", totalElo=" + totalElo +
                ", range=" + range +
                ", time=" + time +
                '}';
    }
}
