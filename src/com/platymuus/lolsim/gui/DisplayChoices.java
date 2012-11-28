package com.platymuus.lolsim.gui;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: HopkinsJ3232
 * Date: 11/13/12
 * Time: 2:49 PM
 * To change this template use File | Settings | File Templates.
 */
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
}