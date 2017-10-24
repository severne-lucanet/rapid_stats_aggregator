package com.severett.rapid_stats_aggregator.reactor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.Environment;
import reactor.bus.EventBus;

@Component
public class RSAEventBus extends EventBus {

    private final RSAEnvironment environment;

    @Autowired
    public RSAEventBus(RSAEnvironment environment) {
        super(environment.getDispatcher(Environment.THREAD_POOL));
        this.environment = environment;
    }
    
    public RSAEnvironment getEnvironment() {
        return environment;
    }

}
