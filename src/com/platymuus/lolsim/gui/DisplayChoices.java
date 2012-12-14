package com.platymuus.lolsim.gui;

import com.platymuus.lolsim.Simulation;
import com.platymuus.lolsim.matchmaking.MatchQueue;
import com.platymuus.lolsim.players.Summoner;

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
    private JCheckBox numPlayersCheckBox;

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public List<String> getPieces(Simulation sim, MatchQueue queue) {
        String qn = queue.getName();
        ArrayList<String> result = new ArrayList<String>();
        
        int players = 0, eloSum = 0;
        int mostWins = 0, mostLosses = 0;
        int lowestElo = 1200, highestElo = 1200;
        double bestWL = 0;
        
        for (Summoner guy : sim.getSummoners()) { 
            if (guy.getWon(qn) + guy.getLost(qn) == 0) continue;
            ++players;
            
            int elo = guy.getElo(qn);
            eloSum += elo;
            if (guy.getWon(qn) > mostWins) mostWins = guy.getWon(qn);
            if (guy.getLost(qn) > mostLosses) mostLosses = guy.getLost(qn);
            if (elo < lowestElo) lowestElo = elo;
            if (elo > highestElo) highestElo = elo;
            if ((double)guy.getWon(qn) / guy.getLost(qn) > bestWL && guy.getLost(qn) > 1)
                bestWL = (double)guy.getWon(qn) / guy.getLost(qn);
        }

        piece(result, numPlayersCheckBox, "Num Players", players);
        piece(result, averageEloCheckBox, "Average Elo", players == 0 ? 0 : eloSum / players);
        piece(result, mostLosesCheckBox, "Most Losses", mostLosses);
        piece(result, mostWinsCheckBox, "Most Wins", mostWins);
        piece(result, lowestEloCheckBox, "Lowest Elo", lowestElo);
        piece(result, highestEloCheckBox, "Highest Elo", highestElo);
        piece(result, bestWinLossRatioCheckBox, "Best W/L", Math.round(bestWL * 100) / 100.0);
        
        // Players Online
        piece(result, numberOfPlayersOnlineCheckBox, "Players Online", sim.getOnlineSummoners().size());
        
        return result;
    }
    
    private void piece(List<String> l, JCheckBox c, String n, Object v) {
        if (c == null || c.isSelected()) l.add(n + ": " + v);
    }
}