package com.severett.rapid_stats_aggregator.service;

import com.severett.rapid_stats_aggregator.dto.InputDTO;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.bus.Event;
import reactor.fn.Consumer;

@Service
public class StatisticsProcessor implements Consumer<Event<InputDTO<JSONObject>>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsProcessor.class);
    
    @Override
    public void accept(Event<InputDTO<JSONObject>> event) {
        LOGGER.debug("Processing statistics for computer {}", event.getData().getComputerUuid());
    }
    
}
