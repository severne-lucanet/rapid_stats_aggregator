package com.severett.rapid_stats_aggregator.service;

import com.severett.rapid_stats_aggregator.exception.StatsParserException;
import com.severett.rapid_stats_aggregator.exception.UnsupportedVersionException;
import com.severett.rapid_stats_aggregator.model.ComputerStats;
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
    public void parseVersionOneStats() throws JSONException {
        JSONObject inputJSON = new JSONObject();
        inputJSON.put("version", 1);
        inputJSON.put("operatingSystem", "Windows 10");
        inputJSON.put("productVersion", "1.2.3");
        inputJSON.put("processCPULoad", 35.5);
        try {
            ComputerStats parsedStats = computerStatsParser.parseComputerStats(inputJSON);
            assertThat(parsedStats.getOperatingSystem(), is("Windows 10"));
            assertThat(parsedStats.getProductVersion(), is("1.2.3"));
            assertThat(parsedStats.getProcessCPULoad(), is(35.5));
            assertThat(parsedStats.getSystemCPULoad(), nullValue());
            assertThat(parsedStats.getMemoryCapacity(), nullValue());
            assertThat(parsedStats.getMemoryUsage(), nullValue());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    public void parseVersionTwoStats() throws JSONException {
        JSONObject inputJSON = new JSONObject();
        inputJSON.put("version", 2);
        inputJSON.put("operatingSystem", "OSX");
        inputJSON.put("productVersion", "2.4.6");
        inputJSON.put("processCPULoad", 41.2);
        inputJSON.put("systemCPULoad", 60);
        try {
            ComputerStats parsedStats = computerStatsParser.parseComputerStats(inputJSON);
            assertThat(parsedStats.getOperatingSystem(), is("OSX"));
            assertThat(parsedStats.getProductVersion(), is("2.4.6"));
            assertThat(parsedStats.getProcessCPULoad(), is(41.2));
            assertThat(parsedStats.getSystemCPULoad(), is(60.0));
            assertThat(parsedStats.getMemoryCapacity(), nullValue());
            assertThat(parsedStats.getMemoryUsage(), nullValue());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    public void parseVersionThreeStats() throws JSONException {
        JSONObject inputJSON = new JSONObject();
        inputJSON.put("version", 3);
        inputJSON.put("operatingSystem", "Windows 10");
        inputJSON.put("productVersion", "3.6.9");
        inputJSON.put("processCPULoad", 50.5);
        inputJSON.put("systemCPULoad", 85.5);
        inputJSON.put("memoryCapacity", 1000000);
        inputJSON.put("memoryUsage", 100000);
        try {
            ComputerStats parsedStats = computerStatsParser.parseComputerStats(inputJSON);
            assertThat(parsedStats.getOperatingSystem(), is("Windows 10"));
            assertThat(parsedStats.getProductVersion(), is("3.6.9"));
            assertThat(parsedStats.getProcessCPULoad(), is(50.5));
            assertThat(parsedStats.getSystemCPULoad(), is(85.5));
            assertThat(parsedStats.getMemoryCapacity(), is(1000000L));
            assertThat(parsedStats.getMemoryUsage(), is(100000L));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    public void parseIncompleteStats() throws JSONException {
        JSONObject inputJSON = new JSONObject();
        inputJSON.put("version", 3);
        inputJSON.put("operatingSystem", "Windows 10");
        inputJSON.put("productVersion", "3.6.9");
        inputJSON.put("processCPULoad", 50.5);
        inputJSON.put("systemCPULoad", 85.5);
        try {
            ComputerStats parsedStats = computerStatsParser.parseComputerStats(inputJSON);
            assertThat(parsedStats.getOperatingSystem(), is("Windows 10"));
            assertThat(parsedStats.getProductVersion(), is("3.6.9"));
            assertThat(parsedStats.getProcessCPULoad(), is(50.5));
            assertThat(parsedStats.getSystemCPULoad(), is(85.5));
            assertThat(parsedStats.getMemoryCapacity(), nullValue());
            assertThat(parsedStats.getMemoryUsage(), nullValue());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    public void parseBadStats() throws JSONException {
        JSONObject inputJSON = new JSONObject();
        inputJSON.put("version", 3);
        inputJSON.put("operatingSystem", "Windows 10");
        inputJSON.put("productVersion", "3.6.9");
        inputJSON.put("processCPULoad", "Fail");
        inputJSON.put("systemCPULoad", 85.5);
        inputJSON.put("memoryCapacity", 1000000);
        inputJSON.put("memoryUsage", 100000);
        try {
            ComputerStats parsedStats = computerStatsParser.parseComputerStats(inputJSON);
            fail("Expected a StatsParserException, yet none was raised");
        } catch (StatsParserException spe) {
            assertThat(spe.getMessage(), is("Error parsing computer statistics: Bad Format Exception For input string: \"Fail\""));
        } catch (UnsupportedVersionException uve) {
            fail(uve.getMessage());
        }
    }
    
    @Test
    public void parseInvalidVersion() throws JSONException {
        JSONObject inputJSON = new JSONObject();
        inputJSON.put("version", 4);
        inputJSON.put("operatingSystem", "Windows 10");
        inputJSON.put("productVersion", "3.6.9");
        inputJSON.put("processCPULoad", 50.5);
        inputJSON.put("systemCPULoad", 85.5);
        inputJSON.put("memoryCapacity", 1000000);
        inputJSON.put("memoryUsage", 100000);
        try {
            ComputerStats parsedStats = computerStatsParser.parseComputerStats(inputJSON);
            fail("Expected an UnsupportedVersionException, yet none was raised");
        } catch (UnsupportedVersionException uve) {
            assertThat(uve.getMessage(), is("Statistics Version 4 not supported"));
        } catch (StatsParserException spe) {
            fail(spe.getMessage());
        }
    }
}
