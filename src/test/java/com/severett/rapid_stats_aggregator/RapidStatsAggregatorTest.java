package com.severett.rapid_stats_aggregator;

import com.severett.rapid_stats_aggregator.util.TestConstants;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.commons.compress.utils.IOUtils;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(SpringRunner.class)
@SpringBootTest(
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
  classes = MainApp.class)
@AutoConfigureMockMvc
public class RapidStatsAggregatorTest {

    @Autowired
    private MockMvc mvc;
    
    @Value("${com.severett.rapid_stats_aggregator.csv.directory}")
    private String csvDirectoryName;
    
    @Value("${com.severett.rapid_stats_aggregator.csv.compStatsFile}")
    private String csvStatsFileName;
    
    @Value("${com.severett.rapid_stats_aggregator.csv.logsFile}")
    private String csvLogsFileName;
    
    private File csvStatsFile;
    
    private File csvLogsFile;
    
    @Before
    public void setup() {
        csvStatsFile = Paths.get(csvDirectoryName, csvStatsFileName).toFile();
        csvLogsFile = Paths.get(csvDirectoryName, csvLogsFileName).toFile();
        csvStatsFile.delete();
        csvLogsFile.delete();
    }
    
    @Test
    public void sendStatsTest() throws Exception {
        JSONObject abc123Content = new JSONObject();
        abc123Content.put("operatingSystem", "Windows 10");
        abc123Content.put("productVersion", "3.6.9");
        abc123Content.put("processCPULoad", 50.5);
        abc123Content.put("systemCPULoad", 85.5);
        abc123Content.put("memoryCapacity", 1000000);
        abc123Content.put("memoryUsage", 100000);
        
        mvc.perform(post("/stats/abc123/upload_statistics")
                .contentType("application/json")
                .content(abc123Content.toString())
            ).andExpect(status().isOk());
        // Need to wait for the stats to be parsed and persisted, as this
        // is an asynchronous operation
        Thread.sleep(1000);
        JSONObject xyz890Content = new JSONObject();
        xyz890Content.put("operatingSystem", "OSX");
        xyz890Content.put("productVersion", "2.4.6");
        xyz890Content.put("processCPULoad", 35.5);
        xyz890Content.put("systemCPULoad", 60.5);
        xyz890Content.put("memoryCapacity", 1000000);
        xyz890Content.put("memoryUsage", 100000);
        
        mvc.perform(post("/stats/xyz890/upload_statistics")
                .contentType("application/json")
                .content(xyz890Content.toString())
            ).andExpect(status().isOk());
        // Need to wait for the stats to be parsed and persisted, as this
        // is an asynchronous operation
        Thread.sleep(1000);
        String[] csvStatsLines = new String(Files.readAllBytes(csvStatsFile.toPath())).split("\n");
        assertThat(csvStatsLines.length, is(2));
        String[] abc123StatsStr = csvStatsLines[0].split("###");
        assertThat(abc123StatsStr.length, is(8));
        assertThat(abc123StatsStr[0], is("abc123"));
        assertThat(abc123StatsStr[2], is("3.6.9"));
        assertThat(abc123StatsStr[3], is("Windows 10"));
        assertThat(abc123StatsStr[4], is("50.5"));
        assertThat(abc123StatsStr[5], is("85.5"));
        assertThat(abc123StatsStr[6], is("100000"));
        assertThat(abc123StatsStr[7], is("1000000"));
        
        String[] xyz890StatsStr = csvStatsLines[1].split("###");
        assertThat(xyz890StatsStr.length, is(8));
        assertThat(xyz890StatsStr[0], is("xyz890"));
        assertThat(xyz890StatsStr[2], is("2.4.6"));
        assertThat(xyz890StatsStr[3], is("OSX"));
        assertThat(xyz890StatsStr[4], is("35.5"));
        assertThat(xyz890StatsStr[5], is("60.5"));
        assertThat(xyz890StatsStr[6], is("100000"));
        assertThat(xyz890StatsStr[7], is("1000000"));
    }
    
    @Test
    public void setLogFileTest() throws Exception {
        File testLogFile = new File(TestConstants.GOOD_RESOURCES_DIRECTORY.getAbsoluteFile(), "app_log.zip");
        try (InputStream fileInputStream = new FileInputStream(testLogFile)) {
            mvc.perform(post("/stats/abc123/upload_logs")
                .contentType("application/zip")
                .content(IOUtils.toByteArray(fileInputStream))
            ).andExpect(status().isOk());
            Thread.sleep(1000);
            String[] csvStatsLines = new String(Files.readAllBytes(csvLogsFile.toPath())).split("\n");
            assertThat(csvStatsLines.length, is(1));
        }
    }
    
    @Test
    public void sendBadStatsTest() throws Exception {
        JSONObject abc123Content = new JSONObject();
        abc123Content.put("operatingSystem", null);
        abc123Content.put("productVersion", null);
        abc123Content.put("processCPULoad", "Fifty");
        abc123Content.put("systemCPULoad", "Eighty");
        abc123Content.put("memoryCapacity", "One Million");
        abc123Content.put("memoryUsage", "One Hundred Thousand");
        
        mvc.perform(post("/stats/abc123/upload_statistics")
                .contentType("application/json")
                .content(abc123Content.toString())
            ).andExpect(status().isOk());
        
        Thread.sleep(1000);
        assertThat(csvStatsFile.getTotalSpace(), is(0L));
    }
    
    @Test
    public void setNoStatsTest() throws Exception {
        mvc.perform(post("/stats/abc123/upload_statistics")
                .contentType("application/json")
                .content("TEST FAILURE")
            ).andExpect(status().isBadRequest());
    }
    
    @Test
    public void noLogTest() throws Exception {
        File testLogFile = new File(TestConstants.BAD_RESOURCES_DIRECTORY.getAbsoluteFile(), "nothing.zip");
        try (InputStream fileInputStream = new FileInputStream(testLogFile)) {
            mvc.perform(post("/stats/abc123/upload_logs")
                .contentType("application/zip")
                .content(IOUtils.toByteArray(fileInputStream))
            ).andExpect(status().isOk());
            Thread.sleep(1000);
            assertThat(csvLogsFile.getTotalSpace(), is(0L));
        }
    }
    
    @Test
    public void uncompressedLogTest() throws Exception {
        File testLogFile = new File(TestConstants.BAD_RESOURCES_DIRECTORY.getAbsoluteFile(), "uncompressed.txt");
        try (InputStream fileInputStream = new FileInputStream(testLogFile)) {
            mvc.perform(post("/stats/abc123/upload_logs")
                .contentType("application/zip")
                .content(IOUtils.toByteArray(fileInputStream))
            ).andExpect(status().isOk());
            Thread.sleep(1000);
            assertThat(csvLogsFile.getTotalSpace(), is(0L));
        }
    }
}
