package com.severett.rapid_stats_aggregator.model;

import java.time.Instant;
import javax.validation.constraints.NotNull;

public abstract class PersistenceEntity {
    
    @NotNull
    private String computerUuid;
    
    @NotNull
    private Instant timeReceived;
    
    public PersistenceEntity() {
    }
    
    public PersistenceEntity(String computerUuid, Instant timeReceived) {
        this.computerUuid = computerUuid;
        this.timeReceived = timeReceived;
    }
    
    public String getComputerUuid() {
        return computerUuid;
    }
    
    public void setComputerUuid(String computerUuid) {
        this.computerUuid = computerUuid;
    }
    
    public Instant getTimeReceived() {
        return timeReceived;
    }
    
    public void setTimeReceived(Instant timeReceived) {
        this.timeReceived = timeReceived;
    }
    
}
