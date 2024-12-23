package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

// Configuration class
public class LogConfiguration {
    private final Properties properties;
    private static final String DEFAULT_CONFIG_FILE = "logger-config.properties";

    public LogConfiguration() {
        this(DEFAULT_CONFIG_FILE);
    }

    public LogConfiguration(String configFile) {
        properties = loadProperties(configFile);
    }

    private Properties loadProperties(String configFile) {
        Properties props = new Properties();
        try (InputStream input = getConfigStream(configFile)) {
            if (input != null) {
                props.load(input);
            } else {
                throw new IllegalStateException("Configuration file not found: " + configFile);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load configuration", e);
        }
        validateProperties(props);
        return props;
    }

    private InputStream getConfigStream(String configFile) {
        InputStream input = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(configFile);
        if (input == null) {
            input = getClass().getClassLoader().getResourceAsStream(configFile);
        }
        return input;
    }

    private void validateProperties(Properties props) {
        List<String> required = Arrays.asList("log.level", "log.sink.type", "log.file.location");
        List<String> missing = required.stream()
                .filter(prop -> !props.containsKey(prop))
                .collect(Collectors.toList());

        if (!missing.isEmpty()) {
            throw new IllegalStateException("Missing required properties: " + missing);
        }
    }

    public String getString(String key) {
        return properties.getProperty(key);
    }

    public String getString(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public int getInt(String key, int defaultValue) {
        return Integer.parseInt(getString(key, String.valueOf(defaultValue)));
    }

    public long getLong(String key, long defaultValue) {
        return Long.parseLong(getString(key, String.valueOf(defaultValue)));
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return Boolean.parseBoolean(getString(key, String.valueOf(defaultValue)));
    }
}