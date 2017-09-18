package com.severett.rapid_stats_aggregator.controller;

import java.io.InputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stats")
public class StatsAggregatorController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(StatsAggregatorController.class);
    
    @RequestMapping(value = "/{computerUuid}/upload_stats", method = RequestMethod.POST)
    public void uploadStats(HttpServletRequest request, HttpServletResponse response,
            @PathVariable("computerUuid") String computerUuid) {
        LOGGER.debug("Received statistics upload from {}", computerUuid);
        response.setStatus(HttpServletResponse.SC_OK);
    }
    
    @RequestMapping(value = "/{computerUuid}/upload_logs", method = RequestMethod.POST, consumes = "application/zip", produces = "application/json")
    public void uploadLogs(HttpServletRequest request, HttpServletResponse response,
            @PathVariable("computerUuid") String computerUuid, InputStream zipInputStream) {
        LOGGER.debug("Received log file upload from {}", computerUuid);
        response.setStatus(HttpServletResponse.SC_OK);
    }
    
}
