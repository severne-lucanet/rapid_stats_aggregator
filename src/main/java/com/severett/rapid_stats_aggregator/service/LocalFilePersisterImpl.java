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

import com.severett.rapid_stats_aggregator.model.ComputerStats;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("Local")
public class LocalFilePersisterImpl implements Persister {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalFilePersisterImpl.class);
    
    private static final String DELIMITER = "###";
    
    private final Path statsFilePath;
    private final Path logDirectoryPath;
    
    public LocalFilePersisterImpl (
            @Value("${com.severett.rapid_stats_aggregator.lfp.directory}") String csvFileDirectory,
            @Value("${com.severett.rapid_stats_aggregator.lfp.compStatsFile}") String compStatsFile,
            @Value("${com.severett.rapid_stats_aggregator.lfp.logsDirectory}") String logsDirectory
    ) throws IOException {
        Files.createDirectories(Paths.get(csvFileDirectory));
        statsFilePath = Paths.get(csvFileDirectory, compStatsFile);
        logDirectoryPath = Paths.get(csvFileDirectory, logsDirectory);
        Files.createDirectories(logDirectoryPath);
    }
    
    @Override
    public void saveComputerStats(ComputerStats computerStats) {
        StringBuilder sb = new StringBuilder();
        sb.append(computerStats.getComputerUuid()).append(DELIMITER);
        sb.append(computerStats.getTimestamp()).append(DELIMITER);
        sb.append(computerStats.getProductVersion()).append(DELIMITER);
        sb.append(computerStats.getOperatingSystem()).append(DELIMITER);
        sb.append(computerStats.getProcessCPULoad()).append(DELIMITER);
        sb.append(computerStats.getSystemCPULoad()).append(DELIMITER);
        sb.append(computerStats.getMemoryUsage()).append(DELIMITER);
        sb.append(computerStats.getMemoryCapacity()).append("\n");
        
        try {
            LOGGER.debug("Persisting computer stats for {} to {}", computerStats.getComputerUuid(), statsFilePath);
            synchronized (statsFilePath) {
                Files.write(statsFilePath, sb.toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            }
            LOGGER.debug("Finished persisting stats for {}", computerStats.getComputerUuid());
        } catch (IOException ioe) {
            LOGGER.error("Error writting to {}: {}", statsFilePath, ioe.getMessage());
        }
    }

    @Override
    public void saveLogFile(File logFile) throws IOException {
        String logFileName = logFile.getName();
        Path outputPath = Paths.get(logDirectoryPath.toString(), logFileName);
        Files.copy(logFile.toPath(), outputPath, StandardCopyOption.REPLACE_EXISTING);
    }
    
}
