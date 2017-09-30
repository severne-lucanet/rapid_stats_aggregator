package com.severett.rapid_stats_aggregator.model;

import java.time.Instant;
import javax.validation.constraints.NotNull;

public abstract class PersistenceEntity {
    
    @NotNull
    private String computerUuid;
    
    @NotNull
    private Instant timestamp;
    
    public PersistenceEntity() {
    }
    
    public PersistenceEntity(String computerUuid, Instant timestamp) {
        this.computerUuid = computerUuid;
        this.timestamp = timestamp;
    }
    
    public String getComputerUuid() {
        return computerUuid;
    }
    
    public void setComputerUuid(String computerUuid) {
        this.computerUuid = computerUuid;
    }
    
    public Instant getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
    
}
