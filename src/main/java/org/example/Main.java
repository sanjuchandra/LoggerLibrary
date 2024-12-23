package org.example;

public class Main {
    public static void main(String[] args) {
        // Configure Logger
        Sink fileSink = new FileSink("application.log", 1024 * 1024); // 1 MB max size
        LoggerConfig config = new LoggerConfig("yyyy-MM-dd HH:mm:ss", LogLevel.INFO, fileSink);
        Logger logger = new Logger(config);

        // Log Messages
        // test
        logger.info("MainApp", "Application started");
        logger.warn("MainApp", "Low memory warning");
        logger.error("MainApp", "Failed to connect to database");

        // Simulate Log Rotation
        for (int i = 0; i < 1000; i++) {
            logger.info("MainApp", "Log message " + i);
        }
    }
}