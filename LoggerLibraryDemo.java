import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;



// Abstract Sink
interface Sink {
    void write(String message);
}

// File Sink with Log Rotation
class FileSink implements Sink {
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

// Logger Configuration
class LoggerConfig {
    private final String timestampFormat;
    private final LogLevel logLevel;
    private final Sink sink;

    public LoggerConfig(String timestampFormat, LogLevel logLevel, Sink sink) {
        this.timestampFormat = timestampFormat;
        this.logLevel = logLevel;
        this.sink = sink;
    }

    public String getTimestampFormat() {
        return timestampFormat;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }

    public Sink getSink() {
        return sink;
    }
}

// Logger Implementation
class Logger {
    private final SimpleDateFormat timestampFormatter;
    private final LogLevel logLevel;
    private final Sink sink;

    public Logger(LoggerConfig config) {
        this.timestampFormatter = new SimpleDateFormat(config.getTimestampFormat());
        this.logLevel = config.getLogLevel();
        this.sink = config.getSink();
    }

    public void log(LogLevel level, String namespace, String message) {
        if (level.ordinal() >= logLevel.ordinal()) {
            String timestamp = timestampFormatter.format(new Date());
            String formattedMessage = String.format("%s [%s] [%s] %s", level, timestamp, namespace, message);
            sink.write(formattedMessage);
        }
    }

    public void debug(String namespace, String message) {
        log(LogLevel.DEBUG, namespace, message);
    }

    public void info(String namespace, String message) {
        log(LogLevel.INFO, namespace, message);
    }

    public void warn(String namespace, String message) {
        log(LogLevel.WARN, namespace, message);
    }

    public void error(String namespace, String message) {
        log(LogLevel.ERROR, namespace, message);
    }

    public void fatal(String namespace, String message) {
        log(LogLevel.FATAL, namespace, message);
    }
}

// Main Class to Demonstrate Logger Usage
public class LoggerLibraryDemo {
    public static void main(String[] args) {
        // Configure Logger
        Sink fileSink = new FileSink("application.log", 1024 * 1024); // 1 MB max size
        LoggerConfig config = new LoggerConfig("yyyy-MM-dd HH:mm:ss", LogLevel.INFO, fileSink);
        Logger logger = new Logger(config);

        // Log Messages
        logger.info("MainApp", "Application started");
        logger.warn("MainApp", "Low memory warning");
        logger.error("MainApp", "Failed to connect to database");

        // Simulate Log Rotation
        for (int i = 0; i < 1000; i++) {
            logger.info("MainApp", "Log message " + i);
        }
    }
}
