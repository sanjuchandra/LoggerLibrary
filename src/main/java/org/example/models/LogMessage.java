package org.example.models;

import org.example.LogLevel;

import java.time.LocalDateTime;

public class LogMessage {
    private final String loggerName;
    private final LogLevel level;
    private final String message;
    private final Throwable throwable;
    private final LocalDateTime timestamp;
    private final String threadName;

    public LogMessage(String loggerName, LogLevel level, String message, Throwable throwable) {
        this.loggerName = loggerName;
        this.level = level;
        this.message = message;
        this.throwable = throwable;
        this.timestamp = LocalDateTime.now();
        this.threadName = Thread.currentThread().getName();
    }

    public String getLoggerName() { return loggerName; }
    public LogLevel getLevel() { return level; }
    public String getMessage() { return message; }
    public Throwable getThrowable() { return throwable; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getThreadName() { return threadName; }
}