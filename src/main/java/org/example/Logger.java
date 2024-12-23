package org.example;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    private final SimpleDateFormat timestampFormatter;
    private final LogLevel logLevel;
    private final Sink sink;

    public Logger(LoggerConfig config) {
        this.timestampFormatter = new SimpleDateFormat(config.getTimestampFormat());
        this.logLevel = config.getLogLevel();
        this.sink = config.getSink();
    }

    public void log(LogLevel level, String namespace, String message) {
        if (level.ordinal() >= logLevel.ordinal()) {
            String timestamp = timestampFormatter.format(new Date());
            String formattedMessage = String.format("%s [%s] [%s] %s", level, timestamp, namespace, message);
            sink.write(formattedMessage);
        }
    }

    public void debug(String namespace, String message) {
        log(LogLevel.DEBUG, namespace, message);
    }

    public void info(String namespace, String message) {
        log(LogLevel.INFO, namespace, message);
    }

    public void warn(String namespace, String message) {
        log(LogLevel.WARN, namespace, message);
    }

    public void error(String namespace, String message) {
        log(LogLevel.ERROR, namespace, message);
    }

}
