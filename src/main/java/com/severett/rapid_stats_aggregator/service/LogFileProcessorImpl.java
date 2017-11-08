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

import java.io.IOException;
import java.io.InputStream;

import io.reactivex.disposables.Disposable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.zip.ZipInputStream;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

@Service
public class LogFileProcessorImpl implements LogFileProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogFileProcessorImpl.class);
    
    private final Path tempLogDirectory;
    private final Persister persister;
    private final ExecutorService threadPoolExecutor;
    
    @Autowired
    public LogFileProcessorImpl (
            @Value("${com.severett.rapid_stats_aggregator.tempLogDirectory}") String tempLogDirectory,
            @Value("${com.severett.rapid_stats_aggregator.threadPoolSize.logFiles}") Integer threadPoolSize,
            @Qualifier("getPersister") Persister persister
        ) throws IOException {
        this.tempLogDirectory = Paths.get(tempLogDirectory);
        // Create temporary log directory if it doesn't exist already
        Files.createDirectories(this.tempLogDirectory);
        this.persister = persister;
        this.threadPoolExecutor = Executors.newFixedThreadPool(threadPoolSize);
    }

    @Override
    public void processLogFile(String computerUUID, Long timestamp, InputStream fileStream) throws IOException {
        StringBuilder fileNameBuilder = new StringBuilder();
        fileNameBuilder.append(computerUUID).append(".").append(timestamp).append(".zip");
        Path targetFilePath = Paths.get(tempLogDirectory.toString(), fileNameBuilder.toString());
        if ((new ZipInputStream(fileStream).getNextEntry()) != null) {
            LOGGER.debug("Copying incoming zip file for computer {} to {}", computerUUID, targetFilePath.toString());
            Files.copy(fileStream, targetFilePath, StandardCopyOption.REPLACE_EXISTING);
            Observable.just(targetFilePath.toString())
                    .subscribeOn(Schedulers.from(threadPoolExecutor))
                    .subscribe(this);
        } else {
            throw new IOException(String.format("Log file for computer %s must be a zip file", computerUUID));
        }
    }
    
    /**
     * Process any temporary files that might have not been pushed to the persistence
     * destination for whatever reason (e.g. program interruption, persistence
     * destination unreachable)
     * @throws IOException 
     */
    @Override
    public void processOutstandingFiles() throws IOException {
        LOGGER.debug("Processing any outstanding log files");
        File[] outstandingFiles = tempLogDirectory.toFile().listFiles((d, name) -> name.matches("^\\w+\\.\\d+\\.zip$"));
        Arrays.stream(outstandingFiles).map(file -> file.getAbsolutePath()).collect(Collectors.toList());
        Observable.fromArray(outstandingFiles)
                .map(file -> file.getAbsolutePath())
                .subscribeOn(Schedulers.from(threadPoolExecutor))
                .subscribe(this);
    }

    @Override
    public void onNext(String logFilePath) {
        LOGGER.debug("Processing log file {}", logFilePath);
        File logFile = new File(logFilePath);
        try {
            persister.saveLogFile(logFile);
            if (logFile.delete()) {
                LOGGER.debug("Log file {} successfully removed", logFilePath);
            } else {
                LOGGER.warn("Log file {} could not be removed", logFilePath);
            }
        } catch (IOException ioe) {
            LOGGER.error("Error persisting {}: {}", logFilePath, ioe.getMessage());
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
