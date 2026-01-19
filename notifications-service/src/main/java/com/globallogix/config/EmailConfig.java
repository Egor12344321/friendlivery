package com.globallogix.config;


import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync
public class EmailConfig {

    @Bean
    public Executor emailTaskExecutor(){
        return Executors.newFixedThreadPool(3);
    }
}
