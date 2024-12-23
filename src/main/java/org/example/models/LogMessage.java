package org.example.models;

import org.example.LogLevel;
import org.example.util.HostNameUtil;

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
    private final String hostName;

    public LogMessage(String loggerName, LogLevel level, String message, Throwable throwable) {
        this.loggerName = loggerName;
        this.level = level;
        this.message = message;
        this.throwable = throwable;
        this.timestamp = LocalDateTime.now();
        this.hostName = HostNameUtil.getHostName();
        this.trackingId = UUID.randomUUID().toString();
        this.threadName = Thread.currentThread().getName();
    }

    public String getLoggerName() { return loggerName; }
    public LogLevel getLevel() { return level; }
    public String getMessage() { return message; }
    public Throwable getThrowable() { return throwable; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getHostName() { return hostName; }
    public String getTrackingId() { return trackingId; }
    public String getThreadName() { return threadName; }
}