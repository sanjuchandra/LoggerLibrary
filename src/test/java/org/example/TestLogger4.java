package org.example;

import org.example.logger.Logger;

// Additional logger classes 4-10 follow same pattern
class TestLogger4 implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(TestLogger4.class);

    public void run() {
        for (int i = 0; i < 100; i++) {
            logger.info("Message {} from Logger4", i);
        }
    }
}
