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
