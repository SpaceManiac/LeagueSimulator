package com.platymuus.lolsim.gui;

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

    public List<String> getPieces() {
        ArrayList<String> result = new ArrayList<String>();

        return result;
    }
    
    private void piece(List<String> l, JCheckBox c, String n, Object v) {
        if (c.isSelected()) l.add(n + ": " + v);
    }
}