package com.severett.rapid_stats_aggregator.exception;

public class StatsParserException extends Exception {
    
    public StatsParserException(String reason) {
        super(String.format("Error parsing computer statistics: %s", reason));
    }
    
}
