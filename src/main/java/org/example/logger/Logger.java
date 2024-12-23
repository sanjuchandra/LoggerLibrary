package org.example.logger;

public interface Logger {
    boolean isTraceEnabled();
    boolean isDebugEnabled();
    boolean isInfoEnabled();
    boolean isWarnEnabled();
    boolean isErrorEnabled();

    void trace(String msg);
    void trace(String format, Object arg);
    void trace(String format, Object... arguments);
    void trace(String msg, Throwable t);

    void debug(String msg);
    void debug(String format, Object arg);
    void debug(String format, Object... arguments);
    void debug(String msg, Throwable t);

    void info(String msg);
    void info(String format, Object arg);
    void info(String format, Object... arguments);
    void info(String msg, Throwable t);

    void warn(String msg);
    void warn(String format, Object arg);
    void warn(String format, Object... arguments);
    void warn(String msg, Throwable t);

    void error(String msg);
    void error(String format, Object arg);
    void error(String format, Object... arguments);
    void error(String msg, Throwable t);
}

