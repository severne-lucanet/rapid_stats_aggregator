package com.severett.rapid_stats_aggregator.dto;

public class InputDTO<PayloadType> {
    
    private final String computerUuid;
    private final PayloadType payload;
    
    public InputDTO(String computerUuid, PayloadType payload) {
        this.computerUuid = computerUuid;
        this.payload = payload;
    }
    
    public String getComputerUuid() {
        return computerUuid;
    }
    
    public PayloadType getPayload() {
        return payload;
    }
    
}
