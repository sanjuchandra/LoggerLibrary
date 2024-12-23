package org.example.sink.impl;

import org.example.LogConfiguration;
import org.example.models.LogMessage;
import org.example.sink.Sink;

import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FileSink implements Sink{
    private final String fileLocation;
    private final String timestampFormat;
    private final long maxFileSize;
    private final int maxBackupCount;
    private final boolean isSync;
    private final Lock lock;
    private BufferedWriter writer;

    public FileSink(LogConfiguration config) throws IOException {
        this.fileLocation = config.getString("log.file.location");
        this.timestampFormat = config.getString("log.timestamp.format", "yyyy-MM-dd HH:mm:ss");
        this.maxFileSize = config.getLong("log.file.rotation.size", 10485760);
        this.maxBackupCount = config.getInt("log.file.max.history", 5);
        this.isSync = "SYNC".equals(config.getString("log.write.mode", "SYNC"));
        this.lock = "MULTI".equals(config.getString("log.thread.model", "SINGLE")) ?
                new ReentrantLock() : null;
        this.writer = new BufferedWriter(new FileWriter(fileLocation, true));
    }


    @Override
    public void write(LogMessage message) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(timestampFormat);
        String logEntry = formatLogEntry(message);

        if (lock != null) {
            lock.lock();
            try {
                writer.write(logEntry);
                if (isSync) writer.flush();
            } finally {
                lock.unlock();
            }
        } else {
            rotateLogs();
            writer.write(logEntry);
            if (isSync) writer.flush();
        }
    }
    private String formatLogEntry(LogMessage message) {
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(timestampFormat);

        sb.append(message.getLevel())
                .append(" [").append(message.getTimestamp().format(formatter)).append("] ")
                .append("[").append(message.getThreadName()).append("] ")
                .append("[").append(message.getLoggerName()).append("] ")
                .append(message.getMessage());

        if (message.getThrowable() != null) {
            sb.append("\n");
            StringWriter sw = new StringWriter();
            message.getThrowable().printStackTrace(new PrintWriter(sw));
            sb.append(sw.toString());
        }

        sb.append("\n");
        return sb.toString();
    }

    public void close() throws IOException {
        writer.close();
    }
    private void rotateLogs() {
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