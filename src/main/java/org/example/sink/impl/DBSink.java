package org.example.sink.impl;

import org.example.sink.AbstractSink;
import org.example.LogConfiguration;
import org.example.models.LogMessage;

import java.io.IOException;

public class DBSink extends AbstractSink {

    public DBSink(LogConfiguration config) throws IOException {
        super(config);
    }

    @Override
    public void write(LogMessage message) throws Exception {

    }
}
