package com.severett.rapid_stats_aggregator.dto;

import java.time.Clock;
import java.time.Instant;

public class InputDTO<PayloadType> {
    
    private final String computerUuid;
    private final PayloadType payload;
    private final Instant timeReceived;
    
    public InputDTO(String computerUuid, PayloadType payload) {
        this.computerUuid = computerUuid;
        this.payload = payload;
        this.timeReceived = Clock.systemUTC().instant();
    }
    
    public String getComputerUuid() {
        return computerUuid;
    }
    
    public PayloadType getPayload() {
        return payload;
    }
    
    public Instant getTimeReceived() {
        return timeReceived;
    }
    
}
