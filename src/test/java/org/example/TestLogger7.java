package org.example;

import org.example.logger.Logger;

class TestLogger7 implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(TestLogger7.class);

    public void run() {
        for (int i = 0; i < 100; i++) {
            logger.info("Message {} from Logger7", i);
        }
    }
}
