package com.severett.rapid_stats_aggregator.service;

import com.severett.rapid_stats_aggregator.dto.InputDTO;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.bus.Event;
import reactor.fn.Consumer;

@Service
public class LogFileProcessor implements Consumer<Event<InputDTO<InputStream>>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogFileProcessor.class);
    
    @Override
    public void accept(Event<InputDTO<InputStream>> event) {
        LOGGER.debug("Processing statistics for computer {}", event.getData().getComputerUuid());
    }
    
}
