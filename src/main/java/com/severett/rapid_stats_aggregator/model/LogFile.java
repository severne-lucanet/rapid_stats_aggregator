package com.severett.rapid_stats_aggregator.model;

import java.time.Instant;
import javax.validation.constraints.NotNull;

public class LogFile extends PersistenceEntity {
    
    @NotNull
    private String content;
    
    public LogFile() {
    }
    
    public LogFile(String computerUuid, Instant timeReceived, String content) {
        super(computerUuid, timeReceived);
        this.content = content;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
}
