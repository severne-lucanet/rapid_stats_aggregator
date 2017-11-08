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

import org.json.JSONObject;
    
public class StatsDTO {
    
    private final String computerUuid;
    private final JSONObject stats;
    private final Long timestamp;
    
    public StatsDTO(String computerUuid, JSONObject stats, Long timestamp) {
        this.computerUuid = computerUuid;
        this.stats = stats;
        this.timestamp = timestamp;
    }
    
    public String getComputerUuid() {
        return computerUuid;
    }
    
    public JSONObject getStats() {
        return stats;
    }
    
    public Long getTimestamp() {
        return timestamp;
    }
    
}
