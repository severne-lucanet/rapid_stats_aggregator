package com.severett.rapid_stats_aggregator.service;

import com.severett.rapid_stats_aggregator.dto.InputDTO;

public interface LogFileProcessor {
    
    void processLogFile(InputDTO<byte[]> inputDTO);
    
}
