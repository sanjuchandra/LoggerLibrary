package org.example.sink;

import org.example.models.LogMessage;

public interface Sink {
    void write(LogMessage message) throws Exception;
}