package com.batista.stickies.core.Logs;

// From geeksforgeeks. Because I cannot be bothered making my own, huge thanks to them
// Source: https://www.geeksforgeeks.org/java/java-time-localdatetime-class-in-java/

// Java Program to illustrate LocalDateTime Class by
// Formatting LocalDateTime to string

// Importing all classes from java.time package
import java.time.LocalDateTime;
import java.time.format.*;
import java.util.*;

// Main class
class TimeLog {

    public static String getTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss a");
        return LocalDateTime.now().format(formatter);
    }
    public static String getFileTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        return LocalDateTime.now().format(formatter);
    }
}