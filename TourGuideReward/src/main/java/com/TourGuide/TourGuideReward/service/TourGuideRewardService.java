package com.TourGuide.TourGuideReward.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import rewardCentral.RewardCentral;

@Service
public class TourGuideRewardService {

    private final RewardCentral rewardCentral;

    public TourGuideRewardService(RewardCentral rewardCentral) {
        this.rewardCentral = rewardCentral;
    }

    public Integer getRewardPoints(UUID attractionId, UUID userId) {
        return rewardCentral.getAttractionRewardPoints(attractionId, userId);
    }
    
}
