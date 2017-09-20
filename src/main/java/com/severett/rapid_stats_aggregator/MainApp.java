package com.severett.rapid_stats_aggregator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.severett")
public class MainApp {

    public static void main(String[] args) throws InterruptedException {
        ApplicationContext app = SpringApplication.run(MainApp.class, args);
    }
    
}
