package org.example;

import org.example.sink.Sink;
import org.example.sink.impl.ConsoleSink;
import org.example.sink.impl.DBSink;
import org.example.sink.impl.FileSink;

import java.io.IOException;

public class SinkFactory {

    public static Sink get(final LogConfiguration config) throws IOException {
        final String sinkType = config.getString("log.sink.type", "FILE");
        switch (sinkType.toUpperCase()) {
            case "FILE":
                return new FileSink(config);
            case "DB":
                return new DBSink(config);
            case "CONSOLE":
                return new ConsoleSink(config);
            default:
                throw new IllegalArgumentException("Invalid sink type: " + sinkType);
        }
    }
}
