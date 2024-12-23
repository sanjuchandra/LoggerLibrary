package org.example;

import org.example.sink.Sink;

import java.text.SimpleDateFormat;
import java.util.Date;

// Logger Implementation
class Logger {
    private final SimpleDateFormat timestampFormatter;
    private final LogLevel logLevel;
    private final Sink sink;
    private final String className;

    public Logger(LoggerConfig config, String className) {
        this.timestampFormatter = new SimpleDateFormat(config.getTimestampFormat());
        this.logLevel = config.getLogLevel();
        this.sink = config.getSink();
        this.className = className;
    }

    public void log(LogLevel level, String message) {
        if (level.ordinal() >= logLevel.ordinal()) {
            String timestamp = timestampFormatter.format(new Date());
            String formattedMessage = String.format("%s [%s] [%s] %s", level, timestamp, className, message);
            sink.write(formattedMessage);
        }
    }

    public void debug(String message) {
        log(LogLevel.DEBUG, message);
    }

    public void info(String message) {
        log(LogLevel.INFO, message);
    }

    public void warn(String message) {
        log(LogLevel.WARN, message);
    }

    public void error(String message) {
        log(LogLevel.ERROR, message);
    }

    public void fatal(String message) {
        log(LogLevel.FATAL, message);
    }
}
