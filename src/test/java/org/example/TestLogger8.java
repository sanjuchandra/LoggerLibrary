package org.example;

import org.example.logger.Logger;

class TestLogger8 implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(TestLogger8.class);

    public void run() {
        for (int i = 0; i < 100; i++) {
            logger.info("Message {} from Logger8", i);
        }
    }
}