package com.severett.rapid_stats_aggregator.service;

import com.severett.rapid_stats_aggregator.model.ComputerStats;
import com.severett.rapid_stats_aggregator.model.LogFile;

public interface Persister {

    public void saveComputerStats(ComputerStats computerStats);
    
    public void saveLogFile(LogFile logFile);
    
}
