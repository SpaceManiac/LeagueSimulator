package com.platymuus.lolsim;

import com.platymuus.lolsim.gui.GuiFrontend;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Main class - process command-line parameters and start the sim as needed
 */
public class Start {

    public static void main(String[] args) {
        Simulation sim = new Simulation();
        
        args = new String[] { "-default", "-runfor=1d" };
        
        Pattern pattern = Pattern.compile("-{0,2}([-a-zA-Z0-9_]+)(=(.+))?");
        for (String arg : args) {
            Matcher m = pattern.matcher(arg);
            if (!m.matches()) {
                System.err.println("Unable to perform " + arg + ": match failure");
            }
            String param = m.group(1);
            String data = m.group(3);

            if (param.equalsIgnoreCase("default")) {
                sim.defaultPopulate();
            } else if (param.equalsIgnoreCase("runfor")) {
                int mult = 1;
                if (data.endsWith("m")) {
                    mult = 60;
                    data = data.substring(0, data.length() - 1);
                } else if (data.endsWith("h")) {
                    mult = 60 * 60;
                    data = data.substring(0, data.length() - 1);
                } else if (data.endsWith("d")) {
                    mult = 24 * 60 * 60;
                    data = data.substring(0, data.length() - 1);
                }
                int amount;
                try {
                    amount = Integer.parseInt(data) * mult;
                }
                catch (NumberFormatException ex) {
                    System.err.println("Unable to perform " + arg + ": " + ex);
                    continue;
                }
                sim.update(amount);
            }
        }

        GuiFrontend front = new GuiFrontend(sim);
        front.start();
    }

}
