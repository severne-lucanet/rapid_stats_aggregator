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


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Clock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.severett.rapid_stats_aggregator.util.TestConstants;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import static org.hamcrest.core.Is.is;
import org.junit.Assert;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class LogFileProcessorTest {
    
    private static final File TEST_LOG_DIR = new File("C:\\Temp\\LucaNet\\RASTestLogFiles");
    private static final Integer THREAD_POOL_SIZE = 5;

    private LogFileProcessor logFileProcessor;
    
    @Mock
    private Persister persister;
    
    @Before
    public void setup() throws IOException {
        if (TEST_LOG_DIR.exists()) {
            Arrays.stream(TEST_LOG_DIR.listFiles()).forEach(file -> {
                if (file.isFile()) {
                    file.delete();
                }
            });
        }
        logFileProcessor = new LogFileProcessorImpl(TEST_LOG_DIR.getAbsolutePath(), THREAD_POOL_SIZE, persister);
    }
    
    public void shutdown() throws IOException {
        if (TEST_LOG_DIR.exists()) {
            Arrays.stream(TEST_LOG_DIR.listFiles()).forEach(file -> {
                if (file.isFile()) {
                    file.delete();
                }
            });
        }
    }
    
    @Test
    public void createLogFileTest() throws Exception {
        File testLogFile = new File(TestConstants.GOOD_RESOURCES_DIRECTORY.getAbsoluteFile(), "lorem_ipsum.zip");
        try (InputStream inputStream = new FileInputStream(testLogFile)) {
            logFileProcessor.processLogFile("abc123", Clock.systemUTC().millis(), inputStream);
            Thread.sleep(500L);
            verify(persister, times(1)).saveLogFile(any());
        }
    }
    
    @Test
    public void processOutstandingLogsTest() throws Exception {
        File testLogFile = new File(TestConstants.GOOD_RESOURCES_DIRECTORY.getAbsoluteFile(), "lorem_ipsum.zip");
        Path outputPath = Paths.get(TEST_LOG_DIR.getAbsolutePath(), "abc123.123456.zip");
        Files.copy(testLogFile.toPath(), outputPath, StandardCopyOption.REPLACE_EXISTING);
        logFileProcessor.processOutstandingFiles();
        Thread.sleep(500L);
        verify(persister, times(1)).saveLogFile(any());
    }
    
    @Test
    public void noLogFileTest() throws IOException {
        File testLogFile = new File(TestConstants.BAD_RESOURCES_DIRECTORY.getAbsoluteFile(), "nothing.zip");
        try (InputStream inputStream = new FileInputStream(testLogFile)) {
            logFileProcessor.processLogFile("empty123", Clock.systemUTC().millis(), inputStream);
            Assert.fail("Expected processing to fail, but it didn't");
        } catch (IOException ioe) {
            assertThat(ioe.getMessage(), is("Log file for computer empty123 must be a zip file"));
            verify(persister, times(0)).saveLogFile(any());
        }
    }
    
    @Test
    public void uncompressedLogFileTest() throws IOException {
        File testLogFile = new File(TestConstants.BAD_RESOURCES_DIRECTORY.getAbsoluteFile(), "uncompressed.txt");
        try (InputStream inputStream = new FileInputStream(testLogFile)) {
            logFileProcessor.processLogFile("uncompressed123", Clock.systemUTC().millis(), inputStream);
            Assert.fail("Expected processing to fail, but it didn't");
        } catch (IOException ioe) {
            assertThat(ioe.getMessage(), is("Log file for computer uncompressed123 must be a zip file"));
            verify(persister, times(0)).saveLogFile(any());
        }
    }
    
}
