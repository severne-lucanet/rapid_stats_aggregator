package com.severett.rapid_stats_aggregator.exception;

public class UnsupportedVersionException extends Exception {
    
    public UnsupportedVersionException(int version) {
        super(String.format("Statistics Version %d not supported", version));
    }
    
}
