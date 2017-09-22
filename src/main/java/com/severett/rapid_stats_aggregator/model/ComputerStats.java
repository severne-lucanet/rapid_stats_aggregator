package com.severett.rapid_stats_aggregator.model;

public class ComputerStats {
    
    public static enum StatName {
        OPERATING_SYSTEM("operatingSystem"),
        PRODUCT_VERSION("productVersion"),
        PROCESS_CPU_LOAD("processCPULoad"),
        SYSTEM_CPU_LOAD("systemCPULoad"),
        MEMORY_CAPACITY("memoryCapacity"),
        MEMORY_USAGE("memoryUsage");
        
        private final String jsonName;
        private StatName(String jsonName) {
            this.jsonName = jsonName;
        }

        @Override
        public String toString() {
            return jsonName;
        }
    }

    private String operatingSystem;
    private String productVersion;
    private Double processCPULoad;
    private Double systemCPULoad;
    private Long memoryCapacity;
    private Long memoryUsage;
    
    public ComputerStats() {
    }
    
    public ComputerStats(String operatingSystem, String productVersion, Double cpuCapacity, Double cpuUsage, Long memoryCapacity, Long memoryUsage) {
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
    
    public Double getProcessCPULoad() {
        return processCPULoad;
    }
    
    public void setProcessCPULoad(Double processCPULoad) {
        this.processCPULoad = processCPULoad;
    }
    
    public Double getSystemCPULoad() {
        return systemCPULoad;
    }
    
    public void setSystemCPULoad(Double systemCPULoad) {
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
