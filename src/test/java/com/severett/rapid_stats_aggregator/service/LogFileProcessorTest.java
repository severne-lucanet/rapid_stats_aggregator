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

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Clock;

import org.apache.commons.compress.utils.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;

import com.severett.rapid_stats_aggregator.dto.InputDTO;
import com.severett.rapid_stats_aggregator.util.TestConstants;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;

@RunWith(MockitoJUnitRunner.class)
public class LogFileProcessorTest {

    private LogFileProcessor logFileProcessor;
    
    @Mock
    private Persister persister;
    
    @Mock
    private Appender mockAppender;
    @Captor
    private ArgumentCaptor<LoggingEvent> captorLoggingEvent;
    
    private Logger logger;
    
    @Before
    public void setup() {
        logFileProcessor = new LogFileProcessorImpl(persister);
        logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        logger.addAppender(mockAppender);
    }
    
    @After
    public void teardown() {
        logger.detachAppender(mockAppender);
    }
    
    @Test
    public void createLogFileTest() throws IOException {
        File testLogFile = new File(TestConstants.GOOD_RESOURCES_DIRECTORY.getAbsoluteFile(), "lorem_ipsum.zip");
        try (InputStream inputStream = new FileInputStream(testLogFile)) {
            logFileProcessor.onNext(new InputDTO<>("abc123", IOUtils.toByteArray(inputStream), Clock.systemUTC().instant()));
            verify(mockAppender, times(1)).doAppend(captorLoggingEvent.capture());
        }
    }
    
    @Test
    public void noLogFileTest() throws IOException {
        File testLogFile = new File(TestConstants.BAD_RESOURCES_DIRECTORY.getAbsoluteFile(), "nothing.zip");
        try (InputStream inputStream = new FileInputStream(testLogFile)) {
            logFileProcessor.onNext(new InputDTO<>("abc123", IOUtils.toByteArray(inputStream), Clock.systemUTC().instant()));
            verify(mockAppender, times(2)).doAppend(captorLoggingEvent.capture());
            assertThat(captorLoggingEvent.getAllValues().stream().anyMatch(loggingEvent -> {
                    return loggingEvent.getFormattedMessage().equals("Error processing log data from abc123: archive is not a ZIP archive");
                }), is(true));
        }
    }
    
    @Test
    public void uncompressedLogFileTest() throws IOException {
        File testLogFile = new File(TestConstants.BAD_RESOURCES_DIRECTORY.getAbsoluteFile(), "uncompressed.txt");
        try (InputStream inputStream = new FileInputStream(testLogFile)) {
            logFileProcessor.onNext(new InputDTO<>("abc123", IOUtils.toByteArray(inputStream), Clock.systemUTC().instant()));
            verify(mockAppender, times(2)).doAppend(captorLoggingEvent.capture());
            assertThat(captorLoggingEvent.getAllValues().stream().anyMatch(loggingEvent -> {
                    return loggingEvent.getFormattedMessage().equals("Error processing log data from abc123: archive is not a ZIP archive");
                }), is(true));
        }
    }
    
}
