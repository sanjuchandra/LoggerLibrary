package org.example.sink.impl;

import org.example.sink.AbstractSink;
import org.example.LogConfiguration;
import org.example.models.LogMessage;

import java.io.*;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;

public class FileSink extends AbstractSink implements AutoCloseable {

    private final PriorityBlockingQueue<LogMessage> logQueue;

    public FileSink(LogConfiguration config) throws IOException {
        super(config);
        // Initialize the priority queue with a comparator based on timestamps
        this.logQueue = new PriorityBlockingQueue<LogMessage>(100, Comparator.comparingLong(log -> log.getTimestamp().toEpochSecond(ZoneOffset.UTC)));
    }

    @Override
    public void write(LogMessage message) throws IOException {
        // Add log message to the priority queue
        logQueue.offer(message);
        processLogs(); // Process logs in order
    }

    private void processLogs() throws IOException {
        // Fetch and process logs in order of priority
        LogMessage nextMessage;
        while ((nextMessage = logQueue.poll()) != null) {
            String logEntry = formatLogEntry(nextMessage);
            writer.write(logEntry);
            if (isSync) {
                writer.flush();
            }
        }
    }

    public void close() throws IOException {
        // Process remaining logs before closing
        LogMessage nextMessage;
        while ((nextMessage = logQueue.poll()) != null) {
            String logEntry = formatLogEntry(nextMessage);
            writer.write(logEntry);
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

        try (FileInputStream fis = new FileInputStream(file); FileOutputStream fos = new FileOutputStream(fileLocation + ".1"); BufferedOutputStream bos = new BufferedOutputStream(fos)) {

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
