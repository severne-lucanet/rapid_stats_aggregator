/**
 * Copyright 2017 Severn Everett
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.severett.rapid_stats_aggregator.controller;

import com.severett.rapid_stats_aggregator.service.LogFileProcessor;
import com.severett.rapid_stats_aggregator.service.StatisticsProcessor;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;


@RestController
@RequestMapping("/stats")
public class StatsAggregatorController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(StatsAggregatorController.class);
    
    private final StatisticsProcessor statisticsProcessor;
    private final LogFileProcessor logFileProcessor;

    public StatsAggregatorController(StatisticsProcessor statisticsProcessor, LogFileProcessor logFileProcessor) throws IOException {
        this.statisticsProcessor = statisticsProcessor;
        this.logFileProcessor = logFileProcessor;
        this.logFileProcessor.processOutstandingFiles();
    }
    
    @RequestMapping(value = "/{computerUuid}/upload_statistics", method = RequestMethod.POST, consumes = "application/json")
    public void uploadStats(HttpServletRequest request, HttpServletResponse response,
            @PathVariable("computerUuid") String computerUuid, @RequestParam("timestamp") Long timestamp, @RequestBody String requestBody) {
        LOGGER.debug("Received statistics upload from {}: {}", computerUuid, requestBody);
        if (timestamp != null) {
            try {
                statisticsProcessor.parseStats(computerUuid, new JSONObject(requestBody), timestamp);
                response.setStatus(HttpServletResponse.SC_OK);
                LOGGER.debug("Finished statistics upload from {}", computerUuid);
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
                logFileProcessor.processLogFile(computerUuid, timestamp, zipInputStream);
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
