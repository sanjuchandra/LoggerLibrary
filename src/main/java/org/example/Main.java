package org.example;

import org.example.sink.Sink;
import org.example.sink.impl.FileSink;

public class Main {
    public static void main(String[] args) {
        // Configure Logger
        // default add
        Sink fileSink = new FileSink("application.log", 1024 * 1024); // 1 MB max size
        LoggerConfig config = new LoggerConfig("yyyy-MM-dd HH:mm:ss", LogLevel.INFO, fileSink);
        LoggerFactory.configure(config);

        // Get Logger
        Logger logger = LoggerFactory.getLogger(Main.class);

        // Log Messages
        logger.info("Application started");
        logger.warn("Low memory warning");
        logger.error("Failed to connect to database");

        // Simulate Log Rotation
        for (int i = 0; i < 10; i++) {
            logger.info("MainApp", "Log message " + i);
        }
    }
}