package org.example.models;

import org.example.LogLevel;

import java.time.LocalDateTime;
import java.util.UUID;

public class LogMessage {
    private final String loggerName;
    private final LogLevel level;
    private final String message;
    private final Throwable throwable;
    private final LocalDateTime timestamp;
    private final String threadName;
    private final String trackingId;

    public LogMessage(String loggerName, LogLevel level, String message, Throwable throwable) {
        this.loggerName = loggerName;
        this.level = level;
        this.message = message;
        this.throwable = throwable;
        this.timestamp = LocalDateTime.now();
        this.trackingId = UUID.randomUUID().toString();
        this.threadName = Thread.currentThread().getName();
    }

    public String getLoggerName() { return loggerName; }
    public LogLevel getLevel() { return level; }
    public String getMessage() { return message; }
    public Throwable getThrowable() { return throwable; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getTrackingId() { return trackingId; }
    public String getThreadName() { return threadName; }
}