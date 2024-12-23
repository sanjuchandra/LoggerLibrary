package org.example.sink.impl;

import org.example.sink.AbstractSink;
import org.example.LogConfiguration;
import org.example.models.LogMessage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class FileSink extends AbstractSink implements AutoCloseable {

    private final BlockingQueue<LogMessage> logQueue;
    private final AtomicBoolean isRunning;
    private final Thread logProcessorThread;

    public FileSink(LogConfiguration config) throws IOException {
        super(config);
        this.logQueue = new LinkedBlockingQueue<>();
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
                    rotateLogs();
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
        File logFile = new File(fileLocation);

        if (logFile.exists() && logFile.length() >= maxFileSize) {
            // Close the current writer
            writer.flush();
            writer.close();

            // Perform log rotation for up to 3 log files
            File rotatedFile2 = new File(fileLocation + ".2.gz");
            File rotatedFile1 = new File(fileLocation + ".1.gz");

            // Step 1: Delete the oldest rotated file if it exists
            if (rotatedFile2.exists()) {
                rotatedFile2.delete();
            }

            // Step 2: Move .1.gz to .2.gz if .1.gz exists
            if (rotatedFile1.exists()) {
                Files.move(rotatedFile1.toPath(), rotatedFile2.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

            // Step 3: Compress the current log file to .1.gz
            compressFile(logFile, rotatedFile1);

            // Step 4: Reinitialize the writer for the new log file
            writer = new BufferedWriter(new FileWriter(fileLocation, true));
        }
    }

    private void compressFile(File source, File destination) throws IOException {
        // Ensure the source file exists
        if (!source.exists()) {
            throw new IOException("Source file does not exist: " + source.getAbsolutePath());
        }

        // Ensure the parent directory for the destination exists
        File parentDir = destination.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                throw new IOException("Failed to create directories for: " + destination.getAbsolutePath());
            }
        }

        try (
                FileInputStream fis = new FileInputStream(source);
                FileOutputStream fos = new FileOutputStream(destination);
                BufferedOutputStream bos = new BufferedOutputStream(new java.util.zip.GZIPOutputStream(fos))
        ) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                bos.write(buffer, 0, length);
            }
            bos.flush();
        }

        // Delete the source file after successful compression
        if (!source.delete()) {
            throw new IOException("Failed to delete the original log file: " + source.getAbsolutePath());
        }
    }


}
