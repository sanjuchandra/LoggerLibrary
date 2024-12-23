package org.example.sink.impl;

import org.example.sink.AbstractSink;
import org.example.LogConfiguration;
import org.example.models.LogMessage;

import java.io.*;

public class FileSink extends AbstractSink implements AutoCloseable {

    public FileSink(LogConfiguration config) throws IOException {
        super(config);
    }


    @Override
    public void write(LogMessage message) throws IOException {
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