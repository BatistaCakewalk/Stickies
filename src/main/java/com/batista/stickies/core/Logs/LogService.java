package com.batista.stickies.core.Logs;

// Enum Imports
// Java Imports
import static java.lang.System.*;

import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class LogService {

    private static final File logFile;
    // I/O
    static {
        Path logsPath;
        if (SystemUtils.IS_OS_WINDOWS) {
            String appData = System.getenv("APPDATA");
            logsPath = Path.of(appData, "Stickies", "logs");
        } else if (SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_LINUX) {
            logsPath = Path.of(System.getProperty("user.home"), ".stickies", "logs");
        } else {
            logsPath = Path.of(System.getProperty("user.home"), ".stickies", "logs");
        }

        File dir = logsPath.toFile();
        logFile = new File(logsPath + File.separator + TimeLog.getFileTime() + ".log");
        if (!dir.exists()) { dir.mkdirs(); }
        try {
            logFile.createNewFile();
            LogService.info("New log file created.");
        } catch (IOException e) {
            LogService.critical("Something went wrong while writing! | RuntimeException");
            throw new RuntimeException(e);
        }
    }

    // The one private method for all printing.
    private static void log(LogLevel level, String message) throws IOException {
        out.println("[" + TimeLog.getTime() + "] [" + level.getLabel() + "] " + message);
        try (FileWriter writer = new FileWriter(logFile, true)) {
            writer.write("[" + TimeLog.getTime() + "] [" + level.getLabel() + "] " + message + "\n");
        }
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
