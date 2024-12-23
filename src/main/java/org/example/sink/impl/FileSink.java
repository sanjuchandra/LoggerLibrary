package org.example.sink.impl;

import org.example.sink.AbstractSink;
import org.example.LogConfiguration;
import org.example.models.LogMessage;

import java.io.*;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class FileSink extends AbstractSink implements AutoCloseable {

    private final PriorityBlockingQueue<LogMessage> logQueue;
    private final AtomicBoolean isRunning;
    private final Thread logProcessorThread;

    public FileSink(LogConfiguration config) throws IOException {
        super(config);
        this.logQueue = new PriorityBlockingQueue<>(100, Comparator.comparingLong(log -> log.getTimestamp().toEpochSecond(ZoneOffset.UTC)));
        this.isRunning = new AtomicBoolean(true);

        // Start the log processing thread
        this.logProcessorThread = new Thread(this::processLogs);
        this.logProcessorThread.start();
    }

    @Override
    public void write(LogMessage message) throws IOException {
        // Add log message to the priority queue
        logQueue.offer(message);
    }

    private void processLogs() {
        while (isRunning.get() || !logQueue.isEmpty()) {
            try {
                // Poll the queue and process the log if available
                LogMessage nextMessage = logQueue.poll();
                if (nextMessage != null) {
                    String logEntry = formatLogEntry(nextMessage);
                    writer.write(logEntry);
                    if (isSync) {
                        writer.flush();
                    }
                } else {
                    // If no logs are available, sleep briefly to avoid busy-waiting
                    Thread.sleep(10);
                }
            } catch (InterruptedException | IOException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }
    }

    @Override
    public void close() throws IOException {
        // Signal the log processor thread to stop and wait for it to finish
        isRunning.set(false);
        try {
            logProcessorThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }

        // Flush remaining logs
        while (!logQueue.isEmpty()) {
            LogMessage nextMessage = logQueue.poll();
            if (nextMessage != null) {
                String logEntry = formatLogEntry(nextMessage);
                writer.write(logEntry);
            }
        }

        writer.flush();
        writer.close();
    }

    public void rotateLogs() throws IOException {
        File file = new File(fileLocation);

        for (int i = 2; i >= 0; i--) {
            File current = new File(fileLocation + (i == 0 ? "" : "." + i));
            File next = new File(fileLocation + "." + (i + 1));

            if (current.exists()) {
                if (i == 2) {
                    current.delete();
                } else {
                    current.renameTo(next);
                }
            }
        }

        try (FileInputStream fis = new FileInputStream(file);
             FileOutputStream fos = new FileOutputStream(fileLocation + ".1");
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                bos.write(buffer, 0, length);
            }

            bos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        file.delete();
    }
}
