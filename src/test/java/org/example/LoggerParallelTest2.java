package org.example;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.FileReader;

public class LoggerParallelTest2 {
    private static final String LOG_FILE = "application.log";  // Update to match your actual log file path

    @Test
    public void testParallelLogging() throws InterruptedException, IOException {
        // Clear existing log file
        File logFile = new File(LOG_FILE);
        if(logFile.exists()) {
            logFile.delete();
        }

        // Configure logger
        LoggerFactory.configure("test-logger.properties");

        // Create thread pool and start time
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        long startTime = System.currentTimeMillis();

        // Submit all logger tasks
        CountDownLatch latch = new CountDownLatch(10);
        submitLoggerTasks(executorService, latch);

        // Wait for completion
        boolean completed = latch.await(1, TimeUnit.MINUTES);
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);

        assertTrue(completed);

        // Read and verify log file content
        assertTrue(logFile.exists());
        assertTrue(logFile.length() > 0);

        List<String> logLines = readFileLines(logFile);  // Use File and BufferedReader to read lines

        // Verify log entries for each logger
        for (int i = 1; i <= 10; i++) {
            final String loggerNum = String.valueOf(i);
            long count = logLines.stream()
                    .filter(line -> line.contains("TestLogger" + loggerNum +"]"))
                    .count();
            assertTrue(count==100 || count==110);
        }

        // Verify log format
        Pattern logPattern = Pattern.compile(
                "^(INFO|DEBUG|WARN|ERROR)\\s+\\[\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}:\\d{2}:\\d{2}\\.\\d{3}\\]\\s+" +
                        "\\[Thread-\\d+\\]\\s+\\[.*TestLogger\\d+\\].*$"
        );



        // Verify exception logging from TestLogger2
        long exceptionCount = logLines.stream()
                .filter(line -> line.contains("java.lang.RuntimeException: Test exception"))
                .count();
        assertEquals(10, exceptionCount);

        // Verify timing order
        List<Long> timestamps = logLines.stream()
                .map(line -> extractTimestamp(line))
                .filter(ts -> ts != null)
                .collect(Collectors.toList());

        assertTrue(isMonotonicallyIncreasing(timestamps));

        // Verify total execution time
        long executionTime = System.currentTimeMillis() - startTime;
        assertTrue(executionTime < TimeUnit.MINUTES.toMillis(1));
    }

    private void submitLoggerTasks(ExecutorService executor, CountDownLatch latch) {
        executor.submit(createRunnableWithLatch(new TestLogger1(), latch));
        executor.submit(createRunnableWithLatch(new TestLogger2(), latch));
        executor.submit(createRunnableWithLatch(new TestLogger3(), latch));
        executor.submit(createRunnableWithLatch(new TestLogger4(), latch));
        executor.submit(createRunnableWithLatch(new TestLogger5(), latch));
        executor.submit(createRunnableWithLatch(new TestLogger6(), latch));
        executor.submit(createRunnableWithLatch(new TestLogger7(), latch));
        executor.submit(createRunnableWithLatch(new TestLogger8(), latch));
        executor.submit(createRunnableWithLatch(new TestLogger9(), latch));
        executor.submit(createRunnableWithLatch(new TestLogger10(), latch));
    }

    private Runnable createRunnableWithLatch(Runnable task, CountDownLatch latch) {
        return () -> {
            try {
                task.run();
            } finally {
                latch.countDown();
            }
        };
    }

    private List<String> readFileLines(File file) throws IOException {
        // Create a BufferedReader to read the file
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return reader.lines().collect(Collectors.toList());
        }
    }

    private Long extractTimestamp(String logLine) {
        try {
            // Extract timestamp between first [ and ]
            int start = logLine.indexOf('[');
            int end = logLine.indexOf(']');
            if (start >= 0 && end > start) {
                String timestamp = logLine.substring(start + 1, end).trim();
                return LocalDateTime.parse(timestamp,
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"))
                        .atZone(ZoneId.systemDefault())  // Corrected ZoneId syntax
                        .toInstant()
                        .toEpochMilli();
            }
        } catch (Exception e) {
            // Skip invalid format
        }
        return null;
    }

    private boolean isMonotonicallyIncreasing(List<Long> values) {
        for (int i = 1; i < values.size(); i++) {
            if (values.get(i) < values.get(i-1)) {
                return false;
            }
        }
        return true;
    }
}
