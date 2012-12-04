package com.platymuus.lolsim.log;

import com.platymuus.lolsim.Simulation;

import java.io.PrintStream;

/**
 * Log listener that prints to an output stream
 */
public class PrintLogListener implements LogListener {
    
    private final PrintStream pos;
    private boolean debug;
    
    public PrintLogListener() {
        this(System.out, false);
    }

    public PrintLogListener(PrintStream pos) {
        this(pos, false);
    }

    public PrintLogListener(boolean debug) {
        this(System.out, debug);
    }

    public PrintLogListener(PrintStream pos, boolean debug) {
        this.pos = pos;
        this.debug = debug;
    }

    @Override
    public void onLog(boolean debug, long time, String logLine) {
        if (debug && !this.debug) return;
        pos.printf("[%s] %s\n", Simulation.formatTime(time), logLine);
    }
}
