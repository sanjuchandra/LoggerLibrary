package org.example;

import org.example.logger.Logger;

class TestLogger3 implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(TestLogger3.class);

    public void run() {
        for (int i = 0; i < 100; i++) {
            logger.warn("Warning message {} from Logger3", i);
        }
    }
}
