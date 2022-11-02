package com.TourGuide.TourGuideReward.service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import rewardCentral.RewardCentral;

@Service
public class TourGuideRewardService {

    private final RewardCentral rewardCentral;

    public TourGuideRewardService(RewardCentral rewardCentral) {
        this.rewardCentral = rewardCentral;
    }

    @Async("asyncExecutor")
    public CompletableFuture<Integer> getRewardPoints(UUID attractionId, UUID userId) {
        return CompletableFuture.completedFuture(rewardCentral.getAttractionRewardPoints(attractionId, userId));
    }
    
}
