package com.platymuus.lolsim.log;

import java.util.logging.Level;

/**
 * An interface for objects listening for simulation log events
 */
public interface LogListener {

    public void onLog(boolean debug, long time, String logLine);

}
