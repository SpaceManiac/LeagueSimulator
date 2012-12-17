package com.platymuus.lolsim.gui;

import com.platymuus.lolsim.Simulation;
import com.platymuus.lolsim.log.LogListener;
import com.platymuus.lolsim.matchmaking.MatchQueue;
import com.platymuus.lolsim.players.Summoner;
import com.platymuus.lolsim.players.SummonerCompare;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

/**
 * A graphical frontend to the simulation.
 */
public class GuiFrontend implements LogListener {

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
     * The quick display selector dialog.
     */
    private DisplayChoices displayChoices;

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

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Failed to set look and feel: " + e);
            e.printStackTrace();
        }
    }

    public void start() {
        sim.addLogListener(this);
        screen = new SimulationInterface();

        screen.timeSlider.addChangeListener(new SliderListener());
        screen.summonerTable.setModel(new SummonerTableModel());
        screen.optDisplayPanel.setAlignmentX(0);

        // Quick display
        displayChoices = new DisplayChoices();
        screen.quickDisplayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                makeFrame("Quick Display Options", displayChoices.getMainPanel());
            }
        });

        // Queue picker
        DefaultComboBoxModel combo = new DefaultComboBoxModel();
        for (MatchQueue queue : sim.getQueues().values()) {
            combo.addElement(queue.getName());
        }
        combo.setSelectedItem("normal5x5");

        screen.queuePicker.setModel(combo);
        screen.queuePicker.addActionListener(new ActionListener() {
            final JComboBox box = screen.queuePicker;
            @Override
            public void actionPerformed(ActionEvent e) {
                ((SummonerTableModel) screen.summonerTable.getModel()).setQueue("" + box.getSelectedItem());
            }
        });

        frame = makeFrame("League Simulator", screen.mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setGameSpeed(60 * 60);
        timer.start();
    }

    public void stop() {
        timer.stop();
        frame.setVisible(false);
    }

    private void setGameSpeed(double speed) {
        double tps = speed; // ticks per second
        if (tps > 50) {
            tps = 50;
        } else if (tps < 1) {
            tps = 1;
        }
        timer.setDelay((int) (1000.0 / tps));
        gameSpeed = speed / tps; // simulated seconds per tick
        screen.timeSlider.setValue((int)(100 * Math.log10(speed)));
    }

    public void postTickActions() {
        // Shows Days:Hours:Minutes:Seconds as the simulation runs
        long time = sim.getTimeElapsed();
        long days = time / 60 / 60 / 24;
        long hours = time / 60 / 60 % 24;
        long minutes = time / 60 % 60;
        long seconds = time % 60;
        String message = String.format("Time elapsed: %dd%02dh%02dm%02ds", days, hours, minutes, seconds);
        screen.timeDisplay.setText(message);

        // Update the summoners table
        ((SummonerTableModel) screen.summonerTable.getModel()).update();
        
        // Update the quick stats
        screen.optDisplayPanel.removeAll();
        boolean first = true;
        for (String s : displayChoices.getPieces(sim, sim.getQueues().get("" + screen.queuePicker.getSelectedItem()))) {
            if (!first) screen.optDisplayPanel.add(new JLabel("~"));
            screen.optDisplayPanel.add(new JLabel(s));
            first = false;
        }
        if (first) screen.optDisplayPanel.add(new JLabel("Select \"Edit Quick Display\" to edit this display"));
        screen.optDisplayPanel.repaint();
    }
    
    private JFrame makeFrame(String title, JPanel panel) {
        JFrame frame = new JFrame(title);
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
        frame.pack();
        return frame;
    }

    @Override
    public void onLog(boolean debug, long time, String logLine) {
        if (!debug) {
            String line = "[" + Simulation.formatTime(time) + "] " + logLine;
            screen.logText.setText(screen.logText.getText() + line + "\n");
        }
    }

    private class TimerAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            sim.update(gameSpeed);
            postTickActions();
        }
    }

    private class SliderListener implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            double log = screen.timeSlider.getValue() / 100.0; // from 0 to 5
            setGameSpeed(Math.pow(10, log));
        }
    }

    private class SummonerTableModel extends AbstractTableModel {
        private final String[] names = { "", "Username", "Elo", "Wins", "Losses", "Activity", "Skill", "LearnedSkill", "Online" };

        private String queue = "normal5x5";
        private Comparator<Summoner> compare = new SummonerCompare.MostElo("normal5x5");
        private ArrayList<Summoner> guys;
        
        private Summoner selected;

        private SummonerTableModel() {
            update();
        }

        public void setQueue(String q) {
            queue = q;
            compare = new SummonerCompare.MostEloHideNoobs(q);
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Summoner guy = guys.get(rowIndex);
            switch(columnIndex) {
                case 0: // rank
                    return rowIndex + 1;
                case 1: // name
                    return guy.getName();
                case 2: // elo
                    return guy.getElo(queue);
                case 3: // wins
                    return guy.getWon(queue);
                case 4: // losses
                    return guy.getLost(queue);
                case 5: // activity
                    return guy.getActivity();
                case 6: // skill
                    return guy.getSkill();
                case 7: // learnedskill
                    return guy.getLearnedSkill();
                case 8:
                    return sim.getOnlineSummoners().contains(guy);
            }
            return "<invalid>";
        }
        
        private void update() {
            int sel = screen.summonerTable.getSelectedRow();
            if (sel >= 0) {
                selected = guys.get(sel);
            }

            guys = sim.getTopPlayers(100, compare);
            fireTableDataChanged();

            if (selected != null) {
                sel = 0;
                for (Summoner guy : guys) {
                    if (guy == selected) {
                        screen.summonerTable.getSelectionModel().setSelectionInterval(sel, sel);
                        break;
                    }
                    sel++;
                }
            }
        }

        @Override
        public int getRowCount() {
            return guys.size();
        }

        @Override
        public int getColumnCount() {
            return names.length;
        }

        @Override
        public String getColumnName(int column) {
            return names[column];
        }

        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }
    }
}
