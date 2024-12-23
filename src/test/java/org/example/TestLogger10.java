package org.example;

import org.example.logger.Logger;

class TestLogger10 implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(TestLogger10.class);

    public void run() {
        for (int i = 0; i < 100; i++) {
            logger.info("Message {} from Logger10", i);
        }
    }
}
