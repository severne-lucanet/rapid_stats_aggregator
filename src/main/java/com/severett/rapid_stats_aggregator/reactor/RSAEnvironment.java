package com.severett.rapid_stats_aggregator.reactor;

import javax.annotation.PreDestroy;
import org.springframework.stereotype.Component;
import reactor.Environment;

@Component
public class RSAEnvironment extends Environment {
    
    public RSAEnvironment() {
        super();
        assignErrorJournal();
    }
    
    @PreDestroy
    public void shutdownEnv() {
        shutdown();
    }
    
}
