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
package com.severett.rapid_stats_aggregator.model;

import javax.validation.constraints.NotNull;

public abstract class PersistenceEntity {
    
    @NotNull
    private String computerUuid;
    
    @NotNull
    private Long timestamp;
    
    public PersistenceEntity() {
    }
    
    public PersistenceEntity(String computerUuid, Long timestamp) {
        this.computerUuid = computerUuid;
        this.timestamp = timestamp;
    }
    
    public String getComputerUuid() {
        return computerUuid;
    }
    
    public void setComputerUuid(String computerUuid) {
        this.computerUuid = computerUuid;
    }
    
    public Long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
    
}
