package com.platymuus.lolsim.matchmaking;

import com.platymuus.lolsim.Start;
import com.platymuus.lolsim.players.Summoner;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Set;

/**
 * Represents an in-progress or complete match between players in a queue.
 */
public class Match {

    private final EnumMap<Team, ArrayList<Summoner>> players = new EnumMap<Team, ArrayList<Summoner>>(Team.class);

    private final String queue;
    
    private final int teamSize;

    private int size = 0;
    
    private int totalElo = 0;
    
    private int range = 20;
    
    private int time = 0;

    public Match(String queue, int teamSize) {
        this.queue = queue;
        this.teamSize = teamSize;

        // Default teams
        for (Team team : Team.values()) {
            players.put(team, new ArrayList<Summoner>());
        }
    }
    
    public boolean canAdd(int number, int elo) {
        return teamChoices(number).size() > 0 &&
                elo > totalElo / size - range &&
                elo < totalElo / size + range;
    }
    
    public void add(Summoner... summoners) {
        ArrayList<Team> choices = teamChoices(summoners.length);
        Team team = choices.get(0);
        
        for (Summoner guy : summoners) {
            players.get(team).add(guy);
            totalElo += guy.getElo(queue);
        }
        totalElo += summoners.length * summoners.length; // Group elo bonus
        size += summoners.length;
    }

    public boolean isComplete() {
        return teamChoices(1).size() == 0;
    }

    public void tick() {
        ++time;
        if (time % 60 == 0) {
            range += 4;
        }
    }

    public Set<Team> getTeams() {
        return players.keySet();
    }

    public ArrayList<Summoner> getPlayers(Team team) {
        return new ArrayList<Summoner>(players.get(team));
    }

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
                ", size="  + size +
                ", totalElo=" + totalElo +
                ", range=" + range +
                ", time=" + time +
                '}';
    }
}
