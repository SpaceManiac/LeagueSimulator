package com.platymuus.lolsim;

import com.platymuus.lolsim.gui.GuiFrontend;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Main class - process command-line parameters and start the sim as needed
 */
public class Start {

    private static Simulation sim;
    
    public static Simulation simulation() {
        return sim;
    }

    public static void main(String[] args) {
        sim = new Simulation();
        
        Pattern pattern = Pattern.compile("-{0,2}([a-z]+)(=(.+))?");
        for (String arg : args) {
            Matcher m = pattern.matcher(arg);
        }

        GuiFrontend front = new GuiFrontend(sim);
    }

}
