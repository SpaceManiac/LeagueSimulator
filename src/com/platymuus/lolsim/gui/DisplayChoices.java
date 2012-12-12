package com.platymuus.lolsim.gui;

import com.platymuus.lolsim.Simulation;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class DisplayChoices {
    private JCheckBox averageEloCheckBox;
    private JCheckBox mostLosesCheckBox;
    private JCheckBox lowestEloCheckBox;
    private JCheckBox highestEloCheckBox;
    private JCheckBox mostWinsCheckBox;
    private JCheckBox bestWinLossRatioCheckBox;
    private JCheckBox worstWinLossRatioCheckBox;
    private JCheckBox numberOfPlayersOnlineCheckBox;
    private JPanel mainPanel;

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public List<String> getPieces(Simulation sim) {
        ArrayList<String> result = new ArrayList<String>();
        piece(result, averageEloCheckBox, "Average Elo", "");
        piece(result, mostLosesCheckBox, "Most Losses", "");
        piece(result, mostWinsCheckBox, "Most Wins", "");
        piece(result, lowestEloCheckBox, "Lowest Elo", "");
        piece(result, highestEloCheckBox, "Highest Elo", "");
        piece(result, bestWinLossRatioCheckBox, "Best W/L", "");
        piece(result, worstWinLossRatioCheckBox, "Worst W/L", "");
        piece(result, numberOfPlayersOnlineCheckBox, "Players Online", sim.getOnlineSummoners().size());
        return result;
    }
    
    private void piece(List<String> l, JCheckBox c, String n, Object v) {
        if (c.isSelected()) l.add(n + ": " + v);
    }
}