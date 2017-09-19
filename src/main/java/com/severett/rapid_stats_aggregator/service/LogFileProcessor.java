package com.severett.rapid_stats_aggregator.service;

import com.severett.rapid_stats_aggregator.dto.InputDTO;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.bus.Event;
import reactor.fn.Consumer;

@Service
public class LogFileProcessor implements Consumer<Event<InputDTO<byte[]>>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogFileProcessor.class);
    
    @Override
    public void accept(Event<InputDTO<byte[]>> event) {
        InputDTO<byte[]> inputDTO = event.getData();
        LOGGER.debug("Processing log files for computer {}", inputDTO.getComputerUuid());
        try (SeekableInMemoryByteChannel inMemoryByteChannel = new SeekableInMemoryByteChannel(inputDTO.getPayload())) {
            try (ZipFile zipFile = new ZipFile(inMemoryByteChannel)) {
                ZipArchiveEntry archiveEntry = zipFile.getEntries().nextElement();
                InputStream inputStream = zipFile.getInputStream(archiveEntry);
            }
        } catch (IOException ioe) {
            LOGGER.error("Error parsing log data from {}: {}", inputDTO.getComputerUuid(), ioe.getMessage());
        }
    }
    
}
