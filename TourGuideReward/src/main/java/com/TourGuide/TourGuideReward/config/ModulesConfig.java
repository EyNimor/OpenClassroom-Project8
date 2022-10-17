package com.TourGuide.TourGuideReward.config;

import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import rewardCentral.RewardCentral;

@Configuration
public class ModulesConfig {

    @Bean
	public RewardCentral getRewardCentral() {
		Locale.setDefault(Locale.ENGLISH);
		return new RewardCentral();
	}
    
}
