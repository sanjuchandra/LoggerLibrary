package org.example;

import org.example.logger.Logger;

class TestLogger6 implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(TestLogger6.class);

    public void run() {
        for (int i = 0; i < 100; i++) {
            logger.info("Message {} from Logger6", i);
        }
    }
}
