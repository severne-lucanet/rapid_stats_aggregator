package com.severett.rapid_stats_aggregator.service;

import com.severett.rapid_stats_aggregator.dto.InputDTO;
import com.severett.rapid_stats_aggregator.exception.StatsParserException;
import com.severett.rapid_stats_aggregator.exception.UnsupportedVersionException;
import com.severett.rapid_stats_aggregator.model.ComputerStats;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.bus.Event;
import reactor.fn.Consumer;

@Service
public class StatisticsProcessor implements Consumer<Event<InputDTO<JSONObject>>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsProcessor.class);
    
    private final ComputerStatsParser computerStatsParser;
    
    @Autowired
    public StatisticsProcessor(ComputerStatsParser computerStatsParser) {
        this.computerStatsParser = computerStatsParser;
    }
    
    @Override
    public void accept(Event<InputDTO<JSONObject>> event) {
        InputDTO<JSONObject> inputDTO = event.getData();
        LOGGER.debug("Processing statistics for computer {}", inputDTO.getComputerUuid());
        try {
            ComputerStats parsedStats = computerStatsParser.parseComputerStats(inputDTO.getPayload());
        } catch (UnsupportedVersionException uve) {
            LOGGER.error("Version error parsing computer stats for computer {}: {}", inputDTO.getComputerUuid(), uve.getMessage());
        } catch (StatsParserException spe) {
            LOGGER.error("Statistics parsing error for computer {}: {}", inputDTO.getComputerUuid(), spe.getMessage());
        }
    }
    
}
