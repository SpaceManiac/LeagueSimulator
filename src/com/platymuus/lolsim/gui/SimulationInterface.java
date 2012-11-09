package com.platymuus.lolsim.gui;

import javax.swing.*;

public class SimulationInterface {
    private JButton button1;
    private JButton removePlayerButton;
    private JButton addPlayerButton;
    private JButton otherOptionsButton1;
    private JButton otherOptionsButton2;
    private JButton optionsButton;
    private JSlider slider1;
    private JSpinner spinner1;
    private JList list1;
    private JTextField textField1;
    private JTextField textField2;
    private JPanel mainPanel;
    private JTextField textField3;

    public JPanel getMainPanel() {
        return mainPanel;
    }
    //TODO Add something that changes everything. Create list of top players.


    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    public JList getList() {
        return list1;
    }

    public JTextField getTime() {
        return textField3;
    }

    public JButton optionsPanel() {
        return optionsButton;
    }

    public JButton choices1() {
        return otherOptionsButton1;
    }

    public JButton choices2() {
        return otherOptionsButton2;
    }

    public JButton addPlayer() {
        return addPlayerButton;
    }
}
