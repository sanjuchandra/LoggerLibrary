package org.example;

public class Main {
    private static final LoggerImpl logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        // Optional: Load custom config file
        LoggerFactory.configure("logger-config.properties");

        logger.info("Application started");
        try {
            // Some business logic
            String userId="124";
            logger.debug("Processing user {}", userId);
        } catch (Exception e) {
            logger.error("Failed to process request", e);
        }

        // Simulate Log Rotation
        for (int i = 0; i < 1000; i++) {
            logger.info("MainApp", "Log message " + i);
        }
    }
}