package com.platymuus.lolsim.gui;

import com.platymuus.lolsim.Simulation;
import com.platymuus.lolsim.players.Summoner;
import com.platymuus.lolsim.players.SummonerCompare;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
     * The timer controlling ticks.
     */
    private Timer timer;

    /**
     * How fast the simulation should run relative to realtime.
     */
    private double gameSpeed;

    /**
     * Initialize a new Gui frontend.
     *
     * @param sim The simulation to control.
     */
    public GuiFrontend(Simulation sim) {
        this.sim = sim;
        
        timer = new Timer(100, new TimerAction());
        setGameSpeed(60);
    }

    private void setGameSpeed(double speed) {
        int ms = (int)(1000.0 / speed);
        if (ms > 1000) {
            ms = 1000;
        } else if (ms < 50) {
            ms = 50;
        }
        timer.setDelay(ms);
        gameSpeed = speed * 1000.0 / ms;
    }

    public void start() {
        screen = new SimulationInterface();
        postTickActions();

        frame = new JFrame("League Simulator");
        frame.add(screen.getMainPanel());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.pack();
        
        timer.start();
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
        long hours = time / 60 / 60 % 24;
        long minutes = time / 60 % 60;
        long seconds = time % 60;
        String message = String.format("Elapsed: %d:%02d:%02d:%02d", days, hours, minutes, seconds);
        timeCount.setText(message);

        players.setListData(passed.toArray());
    }

    private class TimerAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            sim.update(timer.getDelay() * gameSpeed);
            postTickActions();
        }
    }
}
