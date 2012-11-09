package com.platymuus.lolsim;

import com.platymuus.lolsim.matchmaking.Match;
import com.platymuus.lolsim.matchmaking.Team;
import com.platymuus.lolsim.players.Summoner;

import java.util.EnumMap;

/**
 * Handles manipulation of summoner Elo, wins, and losses and other game statistics.
 */
public class StatsEngine {

    // === Properties

    private final EnumMap<Team, Integer> teamWins = new EnumMap<Team, Integer>(Team.class);
    private double gameMinutes = 0;
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
        // General stats
        ++gamesFinished;
        gameMinutes += game.getLength() / 60.0;
        teamWins.put(game.getWinner(), teamWins.get(game.getWinner()) + 1);

        // Stuff for this match
        Match match = game.getMatch();
        String queue = match.getQueue().getName();
        EnumMap<Team, Integer> eloDeltas = calculateElos(match, game.getWinner());

        for (Team team : match.getTeams()) {
            for (Summoner guy : match.getPlayers(team)) {
                guy.learn();

                guy.setElo(queue, guy.getElo(queue) + eloDeltas.get(team));

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

    private EnumMap<Team, Integer> calculateElos(Match match, Team winner) {
        EnumMap<Team, Double> quotients = new EnumMap<Team, Double>(Team.class);
        EnumMap<Team, Integer> deltas = new EnumMap<Team, Integer>(Team.class);
        double quotientSum = 0;

        // TODO: make this level out to 25 for some games
        // TODO: first 10 matches are placement matches & treated as if elo = 1200
        double kConstant = 60;

        for (Team team : match.getTeams()) {
            int elo = 0, people = 0;
            for (Summoner guy : match.getPlayers(team)) {
                elo += guy.getElo(match.getQueue().getName());
                people++;
            }

            double q = Math.pow(10, (elo / people) / 400.0);
            quotients.put(team, q);
            quotientSum += q;
        }
        for (Team team : match.getTeams()) {
            double expected = quotients.get(team) / quotientSum;
            double delta = kConstant * ((winner == team ? 1 : 0) - expected);
            deltas.put(team, (int) Math.round(delta));
        }

        return deltas;
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
