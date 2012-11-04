package com.platymuus.lolsim;

import com.platymuus.lolsim.matchmaking.Team;
import com.platymuus.lolsim.players.Summoner;

import java.util.EnumMap;

/**
 * Handles manipulation of summoner Elo, wins, and losses and other game statistics.
 */
public class StatsEngine {

    // === Properties

    private final EnumMap<Team, Integer> teamWins = new EnumMap<Team, Integer>(Team.class);
    private int online = 0;
    private int offline = 0;
    private int gamesStarted;
    private int gamesFinished;

    // === Oversight

    /**
     * Construct a new, empty stats engine.
     */
    public StatsEngine() {
        clear();
    }

    /**
     * Clear the stats engine.
     */
    private void clear() {
        // Be sure to reset everything
        for (Team team : Team.values()) {
            teamWins.put(team, 0);
        }
        gamesStarted = 0;
        gamesFinished = 0;
    }

    // === Stats entry

    public void playerOffline(Summoner guy) {
        offline++;
        guy.addLogoff();
    }

    public void playerOnline(Summoner guy) {
        online++;
        guy.addLogon();
    }

    public void playerQueued(Summoner guy, String normal5) {
    }

    public void gameStarted(Game game) {
        ++gamesStarted;
    }

    public void gameEnded(Game game) {
        String queue = game.getMatch().getQueue().getName();
        ++gamesFinished;
        teamWins.put(game.getWinner(), teamWins.get(game.getWinner()) + 1);
        // TODO: keep track of how long games last
        for (Team team : game.getMatch().getTeams()) {
            for (Summoner guy : game.getMatch().getPlayers(team)) {
                guy.learn();
                // TODO: elo
                if (team == game.getWinner()) {
                    guy.setWon(queue, guy.getWon(queue) + 1);
                } else {
                    guy.setLost(queue, guy.getLost(queue) + 1);
                }
                if (Math.random() < 0.3) {
                    guy.setWeight(queue, guy.getWeight(queue));
                }
            }
        }
    }

    // === Stats querying


    public int getGamesStarted() {
        return gamesStarted;
    }

    public int getGamesFinished() {
        return gamesFinished;
    }

    public int getOnline() {
        return online;
    }

    public int getOffline() {
        return offline;
    }
}
