package org.example;

import org.example.logger.Logger;

class TestLogger9 implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(TestLogger9.class);

    public void run() {
        for (int i = 0; i < 100; i++) {
            logger.info("Message {} from Logger9", i);
        }
    }
}
