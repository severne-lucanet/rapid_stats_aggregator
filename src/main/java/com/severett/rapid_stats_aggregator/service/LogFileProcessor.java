package com.severett.rapid_stats_aggregator.service;

import com.severett.rapid_stats_aggregator.dto.InputDTO;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Clock;
import java.time.Instant;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.bus.Event;
import reactor.fn.Consumer;

@Service
public class LogFileProcessor implements Consumer<Event<InputDTO<byte[]>>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogFileProcessor.class);
    
    @Value("${com.severett.rapid_stats_aggregator.logdirectory}")
    private String logDirectory;
    
    @Override
    public void accept(Event<InputDTO<byte[]>> event) {
        InputDTO<byte[]> inputDTO = event.getData();
        LOGGER.debug("Processing log files for computer {}", inputDTO.getComputerUuid());
        try (SeekableInMemoryByteChannel inMemoryByteChannel = new SeekableInMemoryByteChannel(inputDTO.getPayload())) {
            try (ZipFile zipFile = new ZipFile(inMemoryByteChannel)) {
                ZipArchiveEntry archiveEntry = zipFile.getEntries().nextElement();
                try (InputStream inputStream = zipFile.getInputStream(archiveEntry)) {
                    Instant now = Clock.systemUTC().instant();
                    Path fileOutputPath = Paths.get(logDirectory, String.format("%s_%d.txt", inputDTO.getComputerUuid(), now.toEpochMilli()));
                    LOGGER.debug("Writing log file to {}", fileOutputPath);
                    Files.copy(inputStream, fileOutputPath, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        } catch (IOException ioe) {
            LOGGER.error("Error parsing log data from {}: {}", inputDTO.getComputerUuid(), ioe.getMessage());
        }
    }
    
}
