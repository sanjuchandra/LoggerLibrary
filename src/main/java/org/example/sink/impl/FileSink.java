package org.example.sink.impl;

import org.example.sink.Sink;

import java.io.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FileSink implements Sink {
    private final String fileLocation;
    private final long maxFileSize;
    private final Lock lock = new ReentrantLock();

    public FileSink(String fileLocation, long maxFileSize) {
        this.fileLocation = fileLocation;
        this.maxFileSize = maxFileSize;
    }

    @Override
    public void write(String message) {
        lock.lock();
        try {
            File file = new File(fileLocation);

            if (file.exists() && file.length() > maxFileSize) {
                rotateLogs();
            }

            try (FileWriter writer = new FileWriter(file, true)) {
                writer.write(message + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
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