package com.severett.rapid_stats_aggregator.parser;

import com.severett.rapid_stats_aggregator.model.ComputerStats;
import java.util.Map;
import java.util.function.BiConsumer;

public interface StatsJSONParserGuidelines {

    public Map<ComputerStats.StatName, BiConsumer<ComputerStats, String>> getGuidelines();
    
}
