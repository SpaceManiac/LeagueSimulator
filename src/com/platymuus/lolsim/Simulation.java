package com.platymuus.lolsim;

import com.platymuus.lolsim.log.PrintLogListener;
import com.platymuus.lolsim.matchmaking.Match;
import com.platymuus.lolsim.matchmaking.MatchQueue;
import com.platymuus.lolsim.matchmaking.Team;
import com.platymuus.lolsim.players.Summoner;
import com.platymuus.lolsim.players.SummonerCompare;
import com.platymuus.lolsim.log.LogListener;

import java.util.*;

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
     * A list of log listeners.
     */
    private final ArrayList<LogListener> logListeners = new ArrayList<LogListener>();

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
     * Add a log listener.
     * @param log The log listener to add.
     */
    public void addLogListener(LogListener log) {
        logListeners.add(log);
    }

    /**
     * Get the randomness generator associated with this simulation.
     *
     * @return The SimRandom object.
     */
    public SimRandom getRandom() {
        return random;
    }

    /**
     * Get how long the simulation has been running in seconds.
     *
     * @return The elapsed time.
     */
    public long getTimeElapsed() {
        return timeElapsed;
    }

    /**
     * Register a match queue with the simulation.
     *
     * @param queue The MatchQueue to register.
     */
    public void addQueue(MatchQueue queue) {
        queues.put(queue.getName(), queue);
    }

    /**
     * Populate the simulation with a few default summoners.
     */
    public void defaultPopulate() {
        addQueue(new MatchQueue(this, "normal5x5", 5, 100));
        addQueue(new MatchQueue(this, "ranked5x5", 5, 20));
        addQueue(new MatchQueue(this, "normal3x3", 3, 10));
        addQueue(new MatchQueue(this, "ranked3x3", 3, 7));
        addQueue(new MatchQueue(this, "dominion5x5", 5, 23));
        addGuys(1000);
    }

    public void addGuys(int num) {
        for (int i = 0; i < num; ++i) {
            summoners.add(new Summoner());
        }
    }

    /**
     * Retrieve the given number of top players as given by the comparator.
     *
     * @param top  The number of players to return.
     * @param comp The Summoner comparator used to find the top players.
     * @return The players ordered by the comparator.
     */
    public ArrayList<Summoner> getTopPlayers(int top, Comparator<Summoner> comp) {
        ArrayList<Summoner> all = new ArrayList<Summoner>(summoners);
        Collections.sort(all, comp);
        int diff = all.size() - top;
        for (int i = 0; i < diff; ++i)
            all.remove(top);
        return all;
    }

    /**
     * Tick the simulation for the given length of simulated time.
     *
     * @param time The time in seconds to tick for.
     */
    public void update(double time) {
        subseconds += time;
        while (subseconds >= 1) {
            tick();
            subseconds -= 1;
            timeElapsed++;
        }
    }

    /**
     * Debugging method.
     */
    public void printStats() {
        log("Games started: " + statsEngine.getGamesStarted());
        log("Games finished: " + statsEngine.getGamesFinished());
        log("Player logons: " + statsEngine.getOnline());
        log("Player logoffs: " + statsEngine.getOffline());
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
                logDebug("Player online: " + guy);
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
                MatchQueue queue = random.weightedRandom(queues.values(), new SimRandom.Weighted<MatchQueue>() {
                    @Override
                    public double weigh(MatchQueue matchQueue) {
                        return matchQueue.getPopularity();
                    }
                });

                queue.addPlayers(guy);
                busySummoners.add(guy);
                statsEngine.playerQueued(guy, queue.getName());
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
                logDebug("Game started: " + game);
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
                logDebug("Game ended: " + game);
                log("Result: " + game.getMatch().getQueue().getName() + "/" + game.getId() + ": " + game.getWinner());
            }
        }
    }

    /**
     * Log an informational message.
     *
     * @param text The message to log.
     */
    public void log(String text) {
        issueLog(false, text);
    }

    /**
     * Log a debug message.
     *
     * @param text The message to log.
     */
    public void logDebug(String text) {
        issueLog(true, text);
    }

    private void issueLog(boolean debug, String text) {
        for (LogListener l : logListeners) {
            l.onLog(debug, timeElapsed, text);
        }
    }

    public static String formatTime(long time) {
        return String.format("%02d:%02d:%02d", time / 60 / 60, time / 60 % 60, time % 60);
    }

    public HashSet<Summoner> getOnlineSummoners() {
        return onlineSummoners;
    }

    public HashMap<String,MatchQueue> getQueues() {
        return queues;
    }


    public HashSet<Summoner> getSummoners() {
        return summoners;
    }
}
