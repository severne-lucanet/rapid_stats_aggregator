package com.severett.rapid_stats_aggregator.service;

import com.severett.rapid_stats_aggregator.dto.InputDTO;
import com.severett.rapid_stats_aggregator.exception.StatsParserException;
import com.severett.rapid_stats_aggregator.model.ComputerStats;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.bus.Event;

@Service
public class StatisticsProcessorImpl implements StatisticsProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsProcessorImpl.class);
    
    private final ComputerStatsParser computerStatsParser;
    private final Persister persister;
    
    @Autowired
    public StatisticsProcessorImpl(ComputerStatsParser computerStatsParser, Persister persister) {
        this.computerStatsParser = computerStatsParser;
        this.persister = persister;
    }
    
    @Override
    public void accept(Event<InputDTO<JSONObject>> event) {
        InputDTO<JSONObject> inputDTO = event.getData();
        String computerUuid = inputDTO.getComputerUuid();
        LOGGER.debug("Processing statistics for computer {}", computerUuid);
        try {
            ComputerStats computerStats = computerStatsParser.parseComputerStats(inputDTO);
            persister.saveComputerStats(computerStats);
        } catch (StatsParserException spe) {
            LOGGER.error("Statistics parsing error for computer {}: {}", computerUuid, spe.getMessage());
        }
    }
    
}
