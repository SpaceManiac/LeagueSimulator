package com.platymuus.lolsim.gui;

import com.platymuus.lolsim.Simulation;
import com.platymuus.lolsim.players.Summoner;
import com.platymuus.lolsim.players.SummonerCompare;

import javax.swing.*;
import java.util.ArrayList;

/**
 * A graphical frontend to the simulation.
 */
public class GuiFrontend {

    /**
     * The simulation being controlled.
     */
    private final Simulation sim;

    /**
     * The form containing the interface.
     */
    private SimulationInterface screen;

    /**
     * The frame containing the form.
     */
    private JFrame frame;

    /**
     * Initialize a new Gui frontend.
     *
     * @param sim The simulation to control.
     */
    public GuiFrontend(Simulation sim) {
        this.sim = sim;
    }

    public void start() {
        screen = new SimulationInterface();
        postTickActions();

        frame = new JFrame("League Simulator");
        frame.add(screen.getMainPanel());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.pack();
    }

    public void postTickActions() {
        JList players = screen.getList();
        ArrayList<String> passed = new ArrayList<String>();
        int count = 0;
        for (Summoner guy : sim.getTopPlayers(30, new SummonerCompare.MostElo("normal5"))) {
            count++;
            passed.add(count + ". " + guy.getName() + " " + guy.getElo("normal5") + ": " + guy.getWon("normal5") + "/" + guy.getLost("normal5"));
        }

        // Shows Days:Hours:Minutes:Seconds as the simulation runs
        JTextField timeCount = screen.getTime();
        long time = sim.getTimeElapsed();
        long days = time / 60 / 60 / 24;
        long hours = (time / 60 / 60) % 24;
        long minutes = (time / 60) % (24 * 60);
        long seconds = (time) % (24 * 60 * 60);
        String message = String.format("Elapsed: %d:%02d:%02d:%02d", days, hours, minutes, seconds);
        timeCount.setText(message);

        players.setListData(passed.toArray());
    }
}
