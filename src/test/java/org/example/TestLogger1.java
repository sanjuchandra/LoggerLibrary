package org.example;

import org.example.logger.Logger;

// Test classes that will log in parallel
class TestLogger1 implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(TestLogger1.class);

    public void run() {
        for (int i = 0; i < 100; i++) {
            logger.info("Test message {} from Logger1", i);
        }
    }
}
