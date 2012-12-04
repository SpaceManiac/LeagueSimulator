package com.platymuus.lolsim.gui;

import com.platymuus.lolsim.Simulation;
import com.platymuus.lolsim.players.Summoner;
import com.platymuus.lolsim.players.SummonerCompare;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.TableHeaderUI;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;

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

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Failed to set look and feel: " + e);
            e.printStackTrace();
        }
    }

    public void start() {
        screen = new SimulationInterface();
        screen.timeSlider.addChangeListener(new SliderListener());

        screen.summonerTable.setModel(new SummonerTableModel());
        screen.summonerTable.getTableHeader().setVisible(true);

        frame = makeFrame("League Simulator", screen.mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setGameSpeed(60 * 60);
        timer.start();
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

        ((SummonerTableModel) screen.summonerTable.getModel()).update();
    }

    private String nameFrom(String description) {
        return description.substring(description.indexOf(". "), description.indexOf(" ", description.indexOf(". ") + 2));
    }
    
    private JFrame makeFrame(String title, JPanel panel) {
        JFrame frame = new JFrame(title);
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
        frame.pack();
        return frame;
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
        private final String[] names = { "", "Username", "Elo", "Won", "Lost", "Activity", "Online" };

        private Comparator<Summoner> compare = new SummonerCompare.MostElo("normal5");
        private ArrayList<Summoner> guys;

        private SummonerTableModel() {
            update();
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
                    return guy.getElo("normal5");
                case 3: // win
                    return guy.getWon("normal5");
                case 4: // losses
                    return guy.getLost("normal5");
                case 5: // activity
                    return guy.getActivity();
                case 6:
                    return sim.getOnlineSummoners().contains(guy);
            }
            return "<invalid>";
        }
        
        private void update() {
            Summoner select = null;
            int sel = screen.summonerTable.getSelectedRow();
            if (sel >= 0) {
                select = guys.get(sel);
            }

            guys = sim.getTopPlayers(30, compare);
            fireTableDataChanged();

            if (select != null) {
                sel = 0;
                for (Summoner guy : guys) {
                    if (guy == select) {
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
