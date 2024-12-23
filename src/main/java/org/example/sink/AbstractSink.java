package org.example.sink;

import org.example.LogConfiguration;
import org.example.models.LogMessage;

import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractSink implements Sink {

    protected final String fileLocation;
    protected final String timestampFormat;
    protected final long maxFileSize;
    protected final int maxBackupCount;
    protected final boolean isSync;
    protected final Lock lock;
    protected BufferedWriter writer;

    public AbstractSink(LogConfiguration config) throws IOException {
        this.fileLocation = config.getString("log.file.location");
        this.timestampFormat = config.getString("log.timestamp.format", "yyyy-MM-dd HH:mm:ss");
        this.maxFileSize = config.getLong("log.file.rotation.size", 10485760);
        this.maxBackupCount = config.getInt("log.file.max.history", 5);
        this.isSync = "SYNC".equals(config.getString("log.write.mode", "SYNC"));
        this.lock = "MULTI".equals(config.getString("log.thread.model", "SINGLE")) ? new ReentrantLock() : null;
        this.writer = new BufferedWriter(new FileWriter(fileLocation, true));
    }

    protected String formatLogEntry(LogMessage message) {
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(timestampFormat);

        sb.append(message.getLevel()).append(" [").append(message.getTimestamp().format(formatter)).append("] ").append("[").append(message.getThreadName()).append("] ").append("[").append(message.getLoggerName()).append("] ").append(message.getMessage());

        if (message.getThrowable() != null) {
            sb.append("\n");
            StringWriter sw = new StringWriter();
            message.getThrowable().printStackTrace(new PrintWriter(sw));
            sb.append(sw);
        }

        sb.append("\n");
        return sb.toString();
    }
}
