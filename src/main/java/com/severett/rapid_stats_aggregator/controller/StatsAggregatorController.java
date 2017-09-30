package com.severett.rapid_stats_aggregator.controller;

import com.severett.rapid_stats_aggregator.dto.InputDTO;
import com.severett.rapid_stats_aggregator.reactor.RSAEventBus;
import com.severett.rapid_stats_aggregator.service.LogFileProcessor;
import com.severett.rapid_stats_aggregator.service.StatisticsProcessor;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.compress.utils.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.bus.Event;
import static reactor.bus.selector.Selectors.$;
import static reactor.bus.selector.Selectors.$;

@RestController
@RequestMapping("/stats")
public class StatsAggregatorController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(StatsAggregatorController.class);
    
    private final RSAEventBus eventBus;
    
    @Autowired
    public StatsAggregatorController(RSAEventBus eventBus, StatisticsProcessor statisticsProcessor, LogFileProcessor logFileProcessor) {
        this.eventBus = eventBus;
        this.eventBus.on($("statistics"), statisticsProcessor);
        this.eventBus.on($("log_files"), logFileProcessor);
    }
    
    @RequestMapping(value = "/{computerUuid}/upload_statistics", method = RequestMethod.POST, consumes = "application/json")
    public void uploadStats(HttpServletRequest request, HttpServletResponse response,
            @PathVariable("computerUuid") String computerUuid, @RequestParam("timestamp") Long timestamp, @RequestBody String requestBody) {
        LOGGER.debug("Received statistics upload from {}", computerUuid);
        if (timestamp != null) {
            try {
                eventBus.notify("statistics", Event.wrap(new InputDTO<JSONObject>(computerUuid, new JSONObject(requestBody), Instant.ofEpochSecond(timestamp))));
                response.setStatus(HttpServletResponse.SC_OK);
            } catch (JSONException jsone) {
                LOGGER.error("Error parsing JSON stats data from {}: {}", computerUuid, jsone.getMessage());
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            } catch (Exception e) {
                LOGGER.error("Unexpected error in uploadStats: {} ({})", e.getMessage(), e.getClass().toString());
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } else {
            LOGGER.warn("Record did not have a timestamp attached - skipping");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
    
    @RequestMapping(value = "/{computerUuid}/upload_logs", method = RequestMethod.POST, consumes = "application/zip")
    public void uploadLogs(HttpServletRequest request, HttpServletResponse response,
            @PathVariable("computerUuid") String computerUuid, @RequestParam("timestamp") Long timestamp, InputStream zipInputStream) {
        LOGGER.debug("Received log file upload from {}", computerUuid);
        if (timestamp != null) {
            try {
                // Need to transfer the input stream in the controller, otherwise
                // the input stream will close when this function terminates
                eventBus.notify("log_files", Event.wrap(new InputDTO<byte[]>(computerUuid, IOUtils.toByteArray(zipInputStream), Instant.ofEpochSecond(timestamp))));
                response.setStatus(HttpServletResponse.SC_OK);
            } catch (IOException ioe) {
                LOGGER.error("Error parsing log data from {}: {}", computerUuid, ioe.getMessage());
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            } catch (Exception e) {
                LOGGER.error("Unexpected error in uploadStats: {} ({})", e.getMessage(), e.getClass().toString());
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } else {
            LOGGER.warn("Log file did not have a timestamp attached - skipping");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
    
}
