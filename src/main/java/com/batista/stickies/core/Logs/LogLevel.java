package com.batista.stickies.core.Logs;

public enum LogLevel {
    INFO("INFO"),
    WARN("WARN"),
    CRITICAL("CRITICAL"),
    FATAL("FATAL"),
    DEBUG("DEBUG");

    private final String label;

    LogLevel(String label) {
        this.label = label;
    }

    public String getLabel() { return label; }
}
