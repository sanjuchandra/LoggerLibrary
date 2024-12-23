package org.example;

import org.junit.Test;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;


public class LoggerParallelTest {
    private static final String LOG_FILE = "application.log";
    @Test
    public void testParallelLogging() throws InterruptedException {
        // Configure logger
        LoggerFactory.configure("test-logger.properties");
        File logFile = new File(LOG_FILE);
        if(logFile.exists()) {
            logFile.delete();
        }


        // Create thread pool
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        // Submit all logger tasks
        executorService.submit(new TestLogger1());
        executorService.submit(new TestLogger2());
        executorService.submit(new TestLogger3());
        executorService.submit(new TestLogger4());
        executorService.submit(new TestLogger5());
        executorService.submit(new TestLogger6());
        executorService.submit(new TestLogger7());
        executorService.submit(new TestLogger8());
        executorService.submit(new TestLogger9());
        executorService.submit(new TestLogger10());

        // Shutdown executor and wait for completion
        executorService.shutdown();
        boolean completed = executorService.awaitTermination(1, TimeUnit.MINUTES);

        assertTrue(completed);

        // Verify log file exists and has content

        assertTrue(logFile.exists());
        assertTrue(logFile.length() > 0);
    }
}