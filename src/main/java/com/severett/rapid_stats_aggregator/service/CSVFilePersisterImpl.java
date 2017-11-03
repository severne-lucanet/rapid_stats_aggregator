package com.severett.rapid_stats_aggregator.service;

import com.severett.rapid_stats_aggregator.model.ComputerStats;
import com.severett.rapid_stats_aggregator.model.LogFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CSVFilePersisterImpl implements Persister {

    private static final Logger LOGGER = LoggerFactory.getLogger(CSVFilePersisterImpl.class);
    
    private static final String DELIMITER = "###";
    
    private final Path statsFilePath;
    private final Path logFilePath;  
    
    public CSVFilePersisterImpl (
            @Value("${com.severett.rapid_stats_aggregator.csv.directory}") String csvFileDirectory,
            @Value("${com.severett.rapid_stats_aggregator.csv.compStatsFile}") String compStatsFile,
            @Value("${com.severett.rapid_stats_aggregator.csv.logsFile}") String logsFile
    ) throws IOException {
        Files.createDirectories(Paths.get(csvFileDirectory));
        statsFilePath = Paths.get(csvFileDirectory, compStatsFile);
        logFilePath = Paths.get(csvFileDirectory, logsFile);
    }
    
    @Override
    public void saveComputerStats(ComputerStats computerStats) {
        StringBuilder sb = new StringBuilder();
        sb.append(computerStats.getComputerUuid()).append(DELIMITER);
        sb.append(computerStats.getTimestamp().getEpochSecond()).append(DELIMITER);
        sb.append(computerStats.getProductVersion()).append(DELIMITER);
        sb.append(computerStats.getOperatingSystem()).append(DELIMITER);
        sb.append(computerStats.getProcessCPULoad()).append(DELIMITER);
        sb.append(computerStats.getSystemCPULoad()).append(DELIMITER);
        sb.append(computerStats.getMemoryUsage()).append(DELIMITER);
        sb.append(computerStats.getMemoryCapacity()).append("\n");
        
        try {
            LOGGER.debug("Persisting computer stats to {}", statsFilePath);
            synchronized (statsFilePath) {
                Files.write(statsFilePath, sb.toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            }
        } catch (IOException ioe) {
            LOGGER.error("Error writting to {}: {}", statsFilePath, ioe.getMessage());
        }
    }

    @Override
    public void saveLogFile(LogFile logFile) {
        StringBuilder sb = new StringBuilder();
        sb.append(logFile.getComputerUuid()).append(DELIMITER);
        sb.append(logFile.getTimestamp().getEpochSecond()).append(DELIMITER);
        sb.append(logFile.getContent().replaceAll("\n", "\t")).append("\n");
        
        try {
            LOGGER.debug("Persisting log entry to {}", logFilePath);
            synchronized (logFilePath) {
                Files.write(logFilePath, sb.toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            }
        } catch (IOException ioe) {
            LOGGER.error("Error writting to {}: {}", logFilePath, ioe.getMessage());
        }
    }
    
}
