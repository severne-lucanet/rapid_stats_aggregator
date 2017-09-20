package com.severett.rapid_stats_aggregator.service;

import com.severett.rapid_stats_aggregator.dto.InputDTO;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.compress.utils.IOUtils;
import static org.hamcrest.Matchers.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.bus.Event;

public class LogFileProcessorTest {

    private LogFileProcessor logFileProcessor;
    
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    
    private File tempDir;
    private static final File GOOD_RESOURCES_DIRECTORY = new File("src/test/resources/good_logs");
    private static final File BAD_RESOURCES_DIRECTORY = new File("src/test/resources/bad_logs");
    
    @Before
    public void setup() throws IOException {
        tempDir = folder.newFolder("tempDir");
        tempDir.deleteOnExit();
        logFileProcessor = new LogFileProcessor();
        ReflectionTestUtils.setField(logFileProcessor, "logDirectory", tempDir.getPath());
    }
    
    @Test
    public void createLogFileTest() throws IOException {
        File testLogFile = new File(GOOD_RESOURCES_DIRECTORY.getAbsoluteFile(), "lorem_ipsum.zip");
        try (InputStream inputStream = new FileInputStream(testLogFile)) {
            logFileProcessor.accept(Event.wrap(new InputDTO<byte[]>("abc123", IOUtils.toByteArray(inputStream))));
            Assert.assertThat(tempDir.listFiles().length, is(1));
        }
    }
    
    @Test
    public void noLogFileTest() throws IOException {
        File testLogFile = new File(BAD_RESOURCES_DIRECTORY.getAbsoluteFile(), "nothing.zip");
        try (InputStream inputStream = new FileInputStream(testLogFile)) {
            logFileProcessor.accept(Event.wrap(new InputDTO<byte[]>("abc123", IOUtils.toByteArray(inputStream))));
            Assert.assertThat(tempDir.listFiles().length, is(0));
        }
    }
    
    @Test
    public void uncompressedLogFileTest() throws IOException {
        File testLogFile = new File(BAD_RESOURCES_DIRECTORY.getAbsoluteFile(), "uncompressed.txt");
        try (InputStream inputStream = new FileInputStream(testLogFile)) {
            logFileProcessor.accept(Event.wrap(new InputDTO<byte[]>("abc123", IOUtils.toByteArray(inputStream))));
            Assert.assertThat(tempDir.listFiles().length, is(0));
        }
    }
    
}
