package com.redtops.stickies.core.Logs;

// Enum Imports
import com.redtops.stickies.core.Logs.LogLevel; // Why is this being said unused?
import com.redtops.stickies.core.Logs.LogType; // Eventually
// Java Imports
import static java.lang.System.*;
import com.redtops.stickies.core.Logs.TimeLog; // Why is this being said unused?
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class LogService {

    private static File logFile;
    // I/O
    static {
        // Variables
        String appData = System.getenv("APPDATA");
        String logsDir = appData + "\\Stickies\\logs\\";

        File dir = new File(logsDir); // Object
        logFile = new File(logsDir + TimeLog.getFileTime() + ".log");

        if (!dir.exists()) {
            dir.mkdirs(); // Makes Directory
        }

        try {
            logFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // The one private method for all printing.
    private static void log(LogLevel level, String message) throws IOException {
        out.println("[" + TimeLog.getTime() + "] [" + level.getLabel() + "] " + message);
        new FileWriter(logFile, true);
    }
    // INFO
    public static void info(String message) {
        try {
            log(LogLevel.INFO, message); // Calls from log
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    // WARN
    public static void warn(String message) {
        try {
            log(LogLevel.WARN, message); // Calls from log
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    // CRITICAL
    public static void critical(String message) {
        try {
            log(LogLevel.CRITICAL, message); // Calls from log
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    // FATAL
    public static void fatal(String message) {
        try {
            log(LogLevel.FATAL, message); // Calls from log
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    // DEBUG
    public static void debug(String message) {
        try {
            log(LogLevel.DEBUG, message); // Calls from log
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



}
