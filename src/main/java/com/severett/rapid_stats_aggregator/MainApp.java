package com.severett.rapid_stats_aggregator;

import java.util.concurrent.CountDownLatch;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import reactor.Environment;
import reactor.bus.EventBus;

@SpringBootApplication
@ComponentScan("com.severett")
public class MainApp {
    
    @Bean
    Environment env() {
        return Environment.initializeIfEmpty().assignErrorJournal();
    }
    
    @Bean
    EventBus createEventBus(Environment env) {
        return EventBus.create(env, Environment.THREAD_POOL);
    }
    
    @Bean
    CountDownLatch latch() {
        return new CountDownLatch(1);
    }

    public static void main(String[] args) throws InterruptedException {
        ApplicationContext app = SpringApplication.run(MainApp.class, args);
        
        app.getBean(CountDownLatch.class).await();
        
        app.getBean(Environment.class).shutdown();
    }
}
