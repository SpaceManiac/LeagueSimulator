package com.platymuus.lolsim.gui;

import com.platymuus.lolsim.Simulation;
import com.platymuus.lolsim.players.Summoner;

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
        screen.getList();
        JList players = screen.getList();
        ArrayList<String> passed = new ArrayList<String>();
        int count = 0;
        for (Summoner guy : sim.getTopPlayers(10)) {
            count++;
            passed.add(count + ". " + guy.getName() + " " + guy.getWon("normal5"));

        }
        players.setListData(passed.toArray());
    }
}
