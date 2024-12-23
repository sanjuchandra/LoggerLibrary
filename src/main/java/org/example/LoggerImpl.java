package org.example;

import org.example.logger.Logger;
import org.example.models.LogMessage;
import org.example.sink.Sink;
import org.example.sink.impl.ConsoleSink;
import org.example.sink.impl.FileSink;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

public class LoggerImpl implements Logger {
    private final String name;
    private final Sink sink;
    private final LogLevel minLevel;

    LoggerImpl(String name) {
        this.name = name;
        LogConfiguration config = LoggerFactory.getConfiguration();
        this.minLevel = LogLevel.valueOf(config.getString("log.level", "INFO"));
        try {
            this.sink = new ConsoleSink(config);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create log sink", e);
        }
    }

    private boolean isLevelEnabled(LogLevel level) {
        return level.getValue() >= minLevel.getValue();
    }

    private void log(LogLevel level, String message, Throwable t) {
        if (!isLevelEnabled(level)) return;

        try {
            sink.write(new LogMessage(name, level, message, t));
        } catch (IOException e) {
            System.err.println("Failed to write log message: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String format(String format, Object... args) {
        if (args == null || args.length == 0) return format;

        StringBuilder result = new StringBuilder();
        int i = 0;
        int j = 0;
        while (i < format.length()) {
            if (format.startsWith("{}", i)) {
                if (j < args.length) {
                    result.append(args[j++]);
                    i += 2;
                } else {
                    result.append(format.charAt(i++));
                }
            } else {
                result.append(format.charAt(i++));
            }
        }
        return result.toString();
    }

    @Override public boolean isTraceEnabled() { return isLevelEnabled(LogLevel.TRACE); }
    @Override public boolean isDebugEnabled() { return isLevelEnabled(LogLevel.DEBUG); }
    @Override public boolean isInfoEnabled() { return isLevelEnabled(LogLevel.INFO); }
    @Override public boolean isWarnEnabled() { return isLevelEnabled(LogLevel.WARN); }
    @Override public boolean isErrorEnabled() { return isLevelEnabled(LogLevel.ERROR); }

    @Override public void trace(String msg) { log(LogLevel.TRACE, msg, null); }
    @Override public void trace(String format, Object arg) { trace(format, new Object[]{arg}); }
    @Override public void trace(String format, Object... arguments) {
        log(LogLevel.TRACE, format(format, arguments), null);
    }
    @Override public void trace(String msg, Throwable t) { log(LogLevel.TRACE, msg, t); }

    @Override public void debug(String msg) { log(LogLevel.DEBUG, msg, null); }
    @Override public void debug(String format, Object arg) { debug(format, new Object[]{arg}); }
    @Override public void debug(String format, Object... arguments) {
        log(LogLevel.DEBUG, format(format, arguments), null);
    }
    @Override public void debug(String msg, Throwable t) { log(LogLevel.DEBUG, msg, t); }

    @Override public void info(String msg) { log(LogLevel.INFO, msg, null); }
    @Override public void info(String format, Object arg) { info(format, new Object[]{arg}); }
    @Override public void info(String format, Object... arguments) {
        log(LogLevel.INFO, format(format, arguments), null);
    }
    @Override public void info(String msg, Throwable t) { log(LogLevel.INFO, msg, t); }

    @Override public void warn(String msg) { log(LogLevel.WARN, msg, null); }
    @Override public void warn(String format, Object arg) { warn(format, new Object[]{arg}); }
    @Override public void warn(String format, Object... arguments) {
        log(LogLevel.WARN, format(format, arguments), null);
    }
    @Override public void warn(String msg, Throwable t) { log(LogLevel.WARN, msg, t); }

    @Override public void error(String msg) { log(LogLevel.ERROR, msg, null); }
    @Override public void error(String format, Object arg) { error(format, new Object[]{arg}); }
    @Override public void error(String format, Object... arguments) {
        log(LogLevel.ERROR, format(format, arguments), null);
    }
    @Override public void error(String msg, Throwable t) { log(LogLevel.ERROR, msg, t); }
}