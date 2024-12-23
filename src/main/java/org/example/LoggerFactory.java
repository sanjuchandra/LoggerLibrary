package org.example;

// Logger Factory
class LoggerFactory {
    private static LoggerConfig config;

    public static void configure(LoggerConfig loggerConfig) {
        config = loggerConfig;
    }

    public static Logger getLogger(Class<?> clazz) {
        if (config == null) {
            throw new IllegalStateException("LoggerFactory is not configured. Call configure() first.");
        }
        return new Logger(config, clazz.getSimpleName());
    }
}
