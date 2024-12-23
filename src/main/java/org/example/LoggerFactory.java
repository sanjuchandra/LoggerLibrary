package org.example;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LoggerFactory {
    private static final Map<String, LoggerImpl> loggers = new ConcurrentHashMap<>();
    private static volatile LogConfiguration configuration;

    static {
        // Load default configuration on startup
        configure(new LogConfiguration());
    }

    public static void configure(String configFile) {
        configure(new LogConfiguration(configFile));
    }

    public static void configure(LogConfiguration config) {
        configuration = config;
    }

    public static LoggerImpl getLogger(Class<?> clazz) {
        return getLogger(clazz.getName());
    }

    public static LoggerImpl getLogger(String name) {
        return loggers.computeIfAbsent(name, LoggerImpl::new);
    }

    static LogConfiguration getConfiguration() {
        return configuration;
    }
}
