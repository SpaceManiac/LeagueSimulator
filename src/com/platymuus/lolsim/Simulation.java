package com.platymuus.lolsim;

import com.platymuus.lolsim.matchmaking.Match;
import com.platymuus.lolsim.matchmaking.MatchQueue;
import com.platymuus.lolsim.matchmaking.Team;
import com.platymuus.lolsim.players.Summoner;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * The meat of the League simulator. Oversees time management, player tracking and queuing, and game handling.
 */
public class Simulation {

    /**
     * The Random associated with this simulation.
     */
    private final SimRandom random = new SimRandom();

    /**
     * The StatsEngine associated with this simulation.
     */
    private final StatsEngine statsEngine = new StatsEngine();

    /**
     * The available queues that players may enter.
     */
    private final HashMap<String, MatchQueue> queues = new HashMap<String, MatchQueue>();

    /**
     * All players contained in the simulation.
     */
    private final HashSet<Summoner> summoners = new HashSet<Summoner>();

    /**
     * Those players that are online in any form.
     */
    private final HashSet<Summoner> onlineSummoners = new HashSet<Summoner>();

    /**
     * Summoners that are "busy" and cannot be taken offline or
     */
    private final HashSet<Summoner> busySummoners = new HashSet<Summoner>();

    /**
     * Summoners to be removed from their online status.
     */
    private final HashSet<Summoner> removeSummoners = new HashSet<Summoner>();

    /**
     * Ongoing games.
     */
    private final HashSet<Game> ongoingGames = new HashSet<Game>();

    /**
     * The amount of time the simulation has been running.
     */
    private long timeElapsed = 0;

    /**
     * The subsecond amount of time the simulation has been running.
     */
    private double subseconds = 0;

    /**
     * Construct a new simulation.
     */
    public Simulation() {

    }

    /**
     * Get the randomness generator associated with this simulation.
     * @return The SimRandom object.
     */
    public SimRandom getRandom() {
        return random;
    }

    /**
     * Register a match queue with the simulation.
     * @param queue The MatchQueue to register.
     */
    public void addQueue(MatchQueue queue) {
        queues.put(queue.getName(), queue);
    }

    /**
     * Populate the simulation with a few default summoners.
     */
    public void defaultPopulate() {
        addQueue(new MatchQueue(this, "normal5", 5, 100));
        for (int i = 0; i < 1000; ++i) {
            Summoner guy = new Summoner();
            guy.setElo("normal5", 1200);
            summoners.add(guy);
        }
    }

    /**
     * Tick the simulation for the given length of simulated time.
     * @param time The time in seconds to tick for.
     */
    public void update(double time) {
        subseconds += time;
        while (subseconds >= 1) {
            tick();
            subseconds -= 1;
            timeElapsed++;
        }
        
        // Temporary
        log("Games started: " + statsEngine.getGamesStarted());
        log("Games finished: " + statsEngine.getGamesFinished());
        
        for (int i = 0; i < 10; ++i) {
            log(SimRandom.generateName());
        }
    }

    /**
     * Tick the simulation for one second. Should not be called directly.
     */
    private void tick() {
        // Put players online or offline as needed
        HashSet<Summoner> remove = new HashSet<Summoner>();
        for (Summoner guy : onlineSummoners) {
            if (busySummoners.contains(guy)) {
                removeSummoners.add(guy);
            } else if (removeSummoners.contains(guy) || random.nextTickChance(60 * 60 * 24 * guy.getActivity())) {
                //log("player offline: " + guy);
                if (removeSummoners.contains(guy)) {
                    removeSummoners.remove(guy);
                }
                remove.add(guy);
                statsEngine.playerOffline(guy);
            }
        }
        for (Summoner guy : summoners) {
            if (onlineSummoners.contains(guy)) continue;
            if (random.nextTickChance(60 * 60 * 24 * (1 - guy.getActivity()))) {
                //log("player online: " + guy);
                onlineSummoners.add(guy);
                statsEngine.playerOnline(guy);
            }
        }
        onlineSummoners.removeAll(remove);

        // Queue players into game
        for (Summoner guy : onlineSummoners) {
            if (busySummoners.contains(guy)) continue;
            if (random.nextTickChance(5 * 60)) {
                //log("player queued: " + guy);
                queues.get("normal5").addPlayers(guy);
                busySummoners.add(guy);
                statsEngine.playerQueued(guy, "normal5");
            }
        }

        // Update match queues
        for (Map.Entry<String, MatchQueue> entry : queues.entrySet()) {
            entry.getValue().tick();

            Match match = null;
            while ((match = entry.getValue().getMatch()) != null) {
                Game game = new Game(this, match);
                ongoingGames.add(game);
                statsEngine.gameStarted(game);
                //log("game started: " + game);
            }
        }

        // Update games
        for (Game game : new HashSet<Game>(ongoingGames)) {
            game.tick();
            if (game.hasEnded()) {
                ongoingGames.remove(game);
                statsEngine.gameEnded(game);
                for (Team team : game.getMatch().getTeams()) {
                    busySummoners.removeAll(game.getMatch().getPlayers(team));
                }
                log("result: " + game.getId() + ": " + game.getWinner());
            }
        }
    }

    /**
     * Log a message along with a simulation timestamp.
     * @param text The message to log.
     */
    private void log(String text) {
        System.out.printf("[%02d:%02d:%02d] %s\n", timeElapsed / 60 / 60, timeElapsed / 60 % 60, timeElapsed % 60, text);
    }

}
