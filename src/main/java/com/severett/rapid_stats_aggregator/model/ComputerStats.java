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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ComputerStats extends PersistenceEntity {
    
    @NotNull
    private String operatingSystem;
    @NotNull
    private String productVersion;
    @NotNull
    @Range(min = 0, max = 100)
    private BigDecimal processCPULoad;
    @Range(min = 0, max = 100)
    private BigDecimal systemCPULoad;
    @Min(value = 0)
    private Long memoryCapacity;
    @Min(value = 0)
    private Long memoryUsage;
    
    public ComputerStats() {
    }
    
    public ComputerStats(String computerUuid, Long timeReceived, String operatingSystem, String productVersion, BigDecimal cpuCapacity, BigDecimal cpuUsage, Long memoryCapacity, Long memoryUsage) {
        super(computerUuid, timeReceived);
        this.operatingSystem = operatingSystem;
        this.productVersion = productVersion;
        this.processCPULoad = cpuCapacity;
        this.systemCPULoad = cpuUsage;
        this.memoryCapacity = memoryCapacity;
        this.memoryUsage = memoryUsage;
    }
    
    public String getOperatingSystem() {
        return operatingSystem;
    }
    
    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }
    
    public String getProductVersion() {
        return productVersion;
    }
    
    public void setProductVersion(String productVersion) {
        this.productVersion = productVersion;
    }
    
    public BigDecimal getProcessCPULoad() {
        return processCPULoad;
    }
    
    public void setProcessCPULoad(BigDecimal processCPULoad) {
        this.processCPULoad = processCPULoad;
    }
    
    public BigDecimal getSystemCPULoad() {
        return systemCPULoad;
    }
    
    public void setSystemCPULoad(BigDecimal systemCPULoad) {
        this.systemCPULoad = systemCPULoad;
    }
    
    public Long getMemoryCapacity() {
        return memoryCapacity;
    }
    
    public void setMemoryCapacity(Long memoryCapacity) {
        this.memoryCapacity = memoryCapacity;
    }
    
    public Long getMemoryUsage() {
        return memoryUsage;
    }
    
    public void setMemoryUsage(Long memoryUsage) {
        this.memoryUsage = memoryUsage;
    }
    
}
