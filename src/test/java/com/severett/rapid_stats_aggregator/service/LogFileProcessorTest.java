package com.severett.rapid_stats_aggregator.service;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import com.severett.rapid_stats_aggregator.dto.InputDTO;
import com.severett.rapid_stats_aggregator.util.TestConstants;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Clock;
import org.apache.commons.compress.utils.IOUtils;
import static org.hamcrest.core.Is.is;
import org.junit.After;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;
import reactor.bus.Event;

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
            logFileProcessor.processLogFile(new InputDTO<>("abc123", IOUtils.toByteArray(inputStream), Clock.systemUTC().instant()));
            verify(mockAppender, times(1)).doAppend(captorLoggingEvent.capture());
        }
    }
    
    @Test
    public void noLogFileTest() throws IOException {
        File testLogFile = new File(TestConstants.BAD_RESOURCES_DIRECTORY.getAbsoluteFile(), "nothing.zip");
        try (InputStream inputStream = new FileInputStream(testLogFile)) {
            logFileProcessor.processLogFile(new InputDTO<>("abc123", IOUtils.toByteArray(inputStream), Clock.systemUTC().instant()));
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
            logFileProcessor.processLogFile(new InputDTO<>("abc123", IOUtils.toByteArray(inputStream), Clock.systemUTC().instant()));
            verify(mockAppender, times(2)).doAppend(captorLoggingEvent.capture());
            assertThat(captorLoggingEvent.getAllValues().stream().anyMatch(loggingEvent -> {
                    return loggingEvent.getFormattedMessage().equals("Error processing log data from abc123: archive is not a ZIP archive");
                }), is(true));
        }
    }
    
}
