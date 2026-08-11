package com.redtops.stickies.core.Logs;

// Enum Imports
import com.redtops.stickies.core.Logs.LogLevel;
import com.redtops.stickies.core.Logs.LogType;
// Java Imports
import static java.lang.System.*;
import com.redtops.stickies.core.Logs.TimeLog;


public class LogService {

    // The one private method for all printing.
    private static void log(LogLevel level, String message) {
        out.println("[" + TimeLog.getTime() + "] [" + level.getLabel() + "] " + message);
    }
    // INFO
    public static void info(String message) {
        log(LogLevel.INFO, message); // Calls from log
    }
    // WARN
    public static void warn(String message) {
        log(LogLevel.WARN, message); // Calls from log
    }
    // CRITICAL
    public static void critical(String message) {
        log(LogLevel.CRITICAL, message); // Calls from log
    }
    // FATAL
    public static void fatal(String message) {
        log(LogLevel.FATAL, message); // Calls from log
    }
    // DEBUG
    public static void debug(String message) {
        log(LogLevel.DEBUG, message); // Calls from log
    }



}
