package com.severett.rapid_stats_aggregator.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import io.reactivex.disposables.Disposable;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.severett.rapid_stats_aggregator.dto.InputDTO;
import com.severett.rapid_stats_aggregator.model.LogFile;

@Service
public class LogFileProcessorImpl implements LogFileProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogFileProcessorImpl.class);
    
    private final Persister persister;
    
    @Autowired
    public LogFileProcessorImpl(Persister persister) {
        this.persister = persister;
    }

    @Override
    public void onNext(InputDTO<byte[]> inputDTO) {
        LOGGER.debug("Processing log files for computer {}", inputDTO.getComputerUuid());
        try (SeekableInMemoryByteChannel inMemoryByteChannel = new SeekableInMemoryByteChannel(inputDTO.getPayload())) {
            try (ZipFile zipFile = new ZipFile(inMemoryByteChannel)) {
                ZipArchiveEntry archiveEntry = zipFile.getEntries().nextElement();
                try (InputStream inputStream = zipFile.getInputStream(archiveEntry)) {
                    String content = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));
                    LogFile logFile = new LogFile(inputDTO.getComputerUuid(), inputDTO.getTimestamp(), content);
                    persister.saveLogFile(logFile);
                }
            }
        } catch (IOException ioe) {
            LOGGER.error("Error processing log data from {}: {}", inputDTO.getComputerUuid(), ioe.getMessage());
        }
    }

    @Override
    public void onError(Throwable error) {
        LOGGER.error("Error in LogFileProcessorImpl: {}", error.getMessage());
    }

    @Override
    public void onComplete() {
        //No-op
    }

    @Override
    public void onSubscribe(Disposable disposable) {
        //No-op
    }
}
