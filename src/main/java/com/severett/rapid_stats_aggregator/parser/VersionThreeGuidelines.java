package com.severett.rapid_stats_aggregator.parser;

import com.severett.rapid_stats_aggregator.model.ComputerStats;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class VersionThreeGuidelines implements StatsJSONParserGuidelines {
    
    private final Map<ComputerStats.StatName, BiConsumer<ComputerStats, String>> guidelinesMap;
    
    public VersionThreeGuidelines() {
        guidelinesMap = new HashMap<>();
        guidelinesMap.put(ComputerStats.StatName.OPERATING_SYSTEM, (compStats, value) -> { compStats.setOperatingSystem(value); });
        guidelinesMap.put(ComputerStats.StatName.PRODUCT_VERSION, (compStats, value) -> { compStats.setProductVersion(value); });
        guidelinesMap.put(ComputerStats.StatName.PROCESS_CPU_LOAD, (compStats, value) -> { 
            try {
                compStats.setProcessCPULoad(new BigDecimal(value));
            } catch (NumberFormatException nfe) {
                throw new NumberFormatException(String.format("Unable to parse '%s' into a decimal", value));
            }
        });
        guidelinesMap.put(ComputerStats.StatName.SYSTEM_CPU_LOAD, (compStats, value) -> { 
            try {
                compStats.setSystemCPULoad(new BigDecimal(value));
            } catch (NumberFormatException nfe) {
                throw new NumberFormatException(String.format("Unable to parse '%s' into a decimal", value));
            }
        });
        guidelinesMap.put(ComputerStats.StatName.MEMORY_CAPACITY, (compStats, value) -> { compStats.setMemoryCapacity(Long.parseLong(value)); });
        guidelinesMap.put(ComputerStats.StatName.MEMORY_USAGE, (compStats, value) -> { compStats.setMemoryUsage(Long.parseLong(value)); });
    }
    
    @Override
    public Map<ComputerStats.StatName, BiConsumer<ComputerStats, String>> getGuidelines() {
        return guidelinesMap;
    }
    
}
