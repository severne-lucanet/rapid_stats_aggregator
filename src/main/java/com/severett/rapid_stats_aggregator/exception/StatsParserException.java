package com.severett.rapid_stats_aggregator.exception;

public class StatsParserException extends Exception {

    private static final long serialVersionUID = -4688128290284081930L;

    public StatsParserException(String reason) {
        super(String.format("Error parsing computer statistics: %s", reason));
    }

}
