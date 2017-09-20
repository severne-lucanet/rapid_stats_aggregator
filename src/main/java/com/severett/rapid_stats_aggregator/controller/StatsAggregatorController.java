package com.severett.rapid_stats_aggregator.controller;

import com.severett.rapid_stats_aggregator.dto.InputDTO;
import com.severett.rapid_stats_aggregator.reactor.RSAEventBus;
import com.severett.rapid_stats_aggregator.service.LogFileProcessor;
import com.severett.rapid_stats_aggregator.service.StatisticsProcessor;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.compress.utils.IOUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.bus.Event;
import static reactor.bus.selector.Selectors.$;

@RestController
@RequestMapping("/stats")
public class StatsAggregatorController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(StatsAggregatorController.class);
    
    private final RSAEventBus eventBus;
    private final StatisticsProcessor statisticsProcessor;
    private final LogFileProcessor logFileProcessor;
    
    @Autowired
    public StatsAggregatorController(RSAEventBus eventBus, StatisticsProcessor statisticsProcessor, LogFileProcessor logFileProcessor) {
        this.eventBus = eventBus;
        this.statisticsProcessor = statisticsProcessor;
        this.logFileProcessor = logFileProcessor;
        this.eventBus.on($("statistics"), statisticsProcessor);
        this.eventBus.on($("log_files"), logFileProcessor);
    }
    
    @RequestMapping(value = "/{computerUuid}/upload_statistics", method = RequestMethod.POST, consumes = "application/json")
    public void uploadStats(HttpServletRequest request, HttpServletResponse response,
            @PathVariable("computerUuid") String computerUuid, @RequestBody String requestBody) {
        LOGGER.debug("Received statistics upload from {}", computerUuid);
        eventBus.notify("statistics", Event.wrap(new InputDTO<JSONObject>(computerUuid, new JSONObject())));
        response.setStatus(HttpServletResponse.SC_OK);
    }
    
    @RequestMapping(value = "/{computerUuid}/upload_logs", method = RequestMethod.POST, consumes = "application/zip")
    public void uploadLogs(HttpServletRequest request, HttpServletResponse response,
            @PathVariable("computerUuid") String computerUuid, InputStream zipInputStream) {
        LOGGER.debug("Received log file upload from {}", computerUuid);
        try {
            eventBus.notify("log_files", Event.wrap(new InputDTO<byte[]>(computerUuid, IOUtils.toByteArray(zipInputStream))));
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (IOException ioe) {
            LOGGER.error("Error parsing log data from {}: {}", computerUuid, ioe.getMessage());
        }
    }
    
}