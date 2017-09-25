package com.severett.rapid_stats_aggregator.service;

import com.severett.rapid_stats_aggregator.dto.InputDTO;
import com.severett.rapid_stats_aggregator.exception.StatsParserException;
import com.severett.rapid_stats_aggregator.model.ComputerStats;
import org.json.JSONObject;

public interface ComputerStatsParser {

    public ComputerStats parseComputerStats(InputDTO<JSONObject> inputDTO) throws StatsParserException;
    
}
