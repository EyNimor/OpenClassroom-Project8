package com.TourGuide.TourGuidePricer.config;

import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import tripPricer.TripPricer;

@Configuration
public class ModulesConfig {

    @Bean
	public TripPricer getTripPricer() {
		Locale.setDefault(Locale.ENGLISH);
		return new TripPricer();
	}
    
}
