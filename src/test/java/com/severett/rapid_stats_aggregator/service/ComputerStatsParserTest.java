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
package com.severett.rapid_stats_aggregator.service;

import com.severett.rapid_stats_aggregator.dto.InputDTO;
import com.severett.rapid_stats_aggregator.exception.StatsParserException;
import com.severett.rapid_stats_aggregator.model.ComputerStats;
import java.math.BigDecimal;
import java.time.Clock;
import static org.hamcrest.core.Is.*;
import static org.hamcrest.core.IsNull.*;
import org.json.JSONException;
import org.json.JSONObject;
import static org.junit.Assert.*;
import org.junit.Test;

public class ComputerStatsParserTest {
    
    private final ComputerStatsParser computerStatsParser;
    
    public ComputerStatsParserTest() {
        computerStatsParser = new ComputerStatsParserImpl();
    }
    
    @Test
    public void parseStats() throws JSONException {
        JSONObject inputJSON = new JSONObject();
        inputJSON.put("operatingSystem", "Windows 10");
        inputJSON.put("productVersion", "3.6.9");
        inputJSON.put("processCPULoad", 50.5);
        inputJSON.put("systemCPULoad", 85.5);
        inputJSON.put("memoryCapacity", 1000000);
        inputJSON.put("memoryUsage", 100000);
        try {
            ComputerStats parsedStats = computerStatsParser.parseComputerStats(new InputDTO<>("abc123", inputJSON, Clock.systemUTC().instant()));
            assertThat(parsedStats.getOperatingSystem(), is("Windows 10"));
            assertThat(parsedStats.getProductVersion(), is("3.6.9"));
            assertThat(parsedStats.getProcessCPULoad(), is(new BigDecimal(50.5)));
            assertThat(parsedStats.getSystemCPULoad(), is(new BigDecimal(85.5)));
            assertThat(parsedStats.getMemoryCapacity(), is(1000000L));
            assertThat(parsedStats.getMemoryUsage(), is(100000L));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    public void parseIncompleteStats() throws JSONException {
        JSONObject inputJSON = new JSONObject();
        inputJSON.put("operatingSystem", "Windows 10");
        inputJSON.put("productVersion", "3.6.9");
        inputJSON.put("processCPULoad", 50.5);
        inputJSON.put("systemCPULoad", 85.5);
        try {
            ComputerStats parsedStats = computerStatsParser.parseComputerStats(new InputDTO<>("abc123", inputJSON, Clock.systemUTC().instant()));
            assertThat(parsedStats.getOperatingSystem(), is("Windows 10"));
            assertThat(parsedStats.getProductVersion(), is("3.6.9"));
            assertThat(parsedStats.getProcessCPULoad(), is(new BigDecimal(50.5)));
            assertThat(parsedStats.getSystemCPULoad(), is(new BigDecimal(85.5)));
            assertThat(parsedStats.getMemoryCapacity(), nullValue());
            assertThat(parsedStats.getMemoryUsage(), nullValue());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    public void parseBadStats() throws JSONException {
        JSONObject inputJSON = new JSONObject();
        inputJSON.put("operatingSystem", "Windows 10");
        inputJSON.put("productVersion", "3.6.9");
        inputJSON.put("processCPULoad", 200.0);
        inputJSON.put("systemCPULoad", -200.0);
        inputJSON.put("memoryCapacity", -5);
        inputJSON.put("memoryUsage", -5);
        try {
            ComputerStats parsedStats = computerStatsParser.parseComputerStats(new InputDTO<>("abc123", inputJSON, Clock.systemUTC().instant()));
            fail("Expected a StatsParserException, yet none was raised");
        } catch (StatsParserException spe) {
            // No-op - expected behavior
        }
    }
    
    @Test
    public void parseInvalidStats() throws JSONException {
        JSONObject inputJSON = new JSONObject();
        inputJSON.put("operatingSystem", "Windows 10");
        inputJSON.put("productVersion", "3.6.9");
        inputJSON.put("processCPULoad", "Fail");
        inputJSON.put("systemCPULoad", 85.5);
        inputJSON.put("memoryCapacity", 1000000);
        inputJSON.put("memoryUsage", 100000);
        try {
            ComputerStats parsedStats = computerStatsParser.parseComputerStats(new InputDTO<>("abc123", inputJSON, Clock.systemUTC().instant()));
            fail("Expected a StatsParserException, yet none was raised");
        } catch (StatsParserException spe) {
            // No-op - expected behavior
        }
    }
    
    @Test
    public void parseUnknownStats() throws JSONException {
        JSONObject inputJSON = new JSONObject();
        inputJSON.put("operatingSystem", "Windows 10");
        inputJSON.put("computerType", "Acer");
        inputJSON.put("productVersion", "3.6.9");
        inputJSON.put("processCPULoad", 65.5);
        inputJSON.put("systemCPULoad", 85.5);
        inputJSON.put("memoryCapacity", 1000000);
        inputJSON.put("memoryUsage", 100000);
        try {
            ComputerStats parsedStats = computerStatsParser.parseComputerStats(new InputDTO<>("abc123", inputJSON, Clock.systemUTC().instant()));
            assertThat(parsedStats.getOperatingSystem(), is("Windows 10"));
            assertThat(parsedStats.getProductVersion(), is("3.6.9"));
            assertThat(parsedStats.getProcessCPULoad(), is(new BigDecimal(65.5)));
            assertThat(parsedStats.getSystemCPULoad(), is(new BigDecimal(85.5)));
            assertThat(parsedStats.getMemoryCapacity(), is(1000000L));
            assertThat(parsedStats.getMemoryUsage(), is(100000L));
        } catch (StatsParserException spe) {
            fail(spe.getMessage());
        }
    }
}
