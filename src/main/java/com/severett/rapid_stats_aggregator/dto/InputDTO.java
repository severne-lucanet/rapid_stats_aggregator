package com.severett.rapid_stats_aggregator.dto;

import java.time.Instant;

public class InputDTO<PayloadType> {
    
    private final String computerUuid;
    private final PayloadType payload;
    private final Instant timestamp;
    
    public InputDTO(String computerUuid, PayloadType payload, Instant timestamp) {
        this.computerUuid = computerUuid;
        this.payload = payload;
        this.timestamp = timestamp;
    }
    
    public String getComputerUuid() {
        return computerUuid;
    }
    
    public PayloadType getPayload() {
        return payload;
    }
    
    public Instant getTimestamp() {
        return timestamp;
    }
    
}
