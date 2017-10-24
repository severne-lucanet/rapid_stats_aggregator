package com.severett.rapid_stats_aggregator.service;

import org.json.JSONObject;

import com.severett.rapid_stats_aggregator.dto.InputDTO;

public interface StatisticsProcessor {
    
    void processStatistics(InputDTO<JSONObject> inputDTO);
    
}
