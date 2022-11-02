package com.TourGuide.TourGuidePricer.config;

import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfiguration {

    private Logger logger = LoggerFactory.getLogger(AsyncConfiguration.class);

    @Bean(name = "asyncExecutor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(200);
        executor.setQueueCapacity(100000);
        executor.setThreadNamePrefix("AsyncThread-");
        executor.initialize();
        logger.info("Executor ready !");
        return executor;
    }
    
}
