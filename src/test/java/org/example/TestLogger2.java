package org.example;

import org.example.logger.Logger;

class TestLogger2 implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(TestLogger2.class);

    public void run() {
        for (int i = 0; i < 100; i++) {
            logger.debug("Debug message {} from Logger2", i);
            if (i % 10 == 0) {
                try {
                    throw new RuntimeException("Test exception");
                } catch (Exception e) {
                    logger.error("Error in Logger2", e);
                }
            }
        }
    }
}
