package com.platymuus.lolsim.frontend;

import com.platymuus.lolsim.Simulation;

/**
 * A front-end to the simulation that uses the command line.
 */
public class ConsoleFrontend {

    private final Simulation simulation;
    
    public ConsoleFrontend(Simulation simulation, String[] args) {
        this.simulation = simulation;
    }
    
}
