package com.severett.rapid_stats_aggregator.service;

import com.severett.rapid_stats_aggregator.exception.StatsParserException;
import com.severett.rapid_stats_aggregator.exception.UnsupportedVersionException;
import com.severett.rapid_stats_aggregator.model.ComputerStats;
import org.json.JSONObject;

public interface ComputerStatsParser {

    public ComputerStats parseComputerStats(String computerUuid, JSONObject statsObject) throws UnsupportedVersionException, StatsParserException;
    
}
