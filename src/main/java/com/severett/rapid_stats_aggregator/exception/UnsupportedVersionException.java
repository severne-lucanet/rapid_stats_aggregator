package com.severett.rapid_stats_aggregator.exception;

public class UnsupportedVersionException extends Exception {

    private static final long serialVersionUID = -5091751500029541119L;

    public UnsupportedVersionException(int version) {
        super(String.format("Statistics Version %d not supported", version));
    }
    
}
