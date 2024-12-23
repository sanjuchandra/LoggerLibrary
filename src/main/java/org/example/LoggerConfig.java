package org.example;

public class LoggerConfig {
    private final String timestampFormat;
    private final LogLevel logLevel;
    private final Sink sink;

    public LoggerConfig(String timestampFormat, LogLevel logLevel, Sink sink) {
        this.timestampFormat = timestampFormat;
        this.logLevel = logLevel;
        this.sink = sink;
    }

    public String getTimestampFormat() {
        return timestampFormat;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }

    public Sink getSink() {
        return sink;
    }
}
