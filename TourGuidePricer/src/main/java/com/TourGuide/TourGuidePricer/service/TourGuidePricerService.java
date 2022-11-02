package com.TourGuide.TourGuidePricer.service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import tripPricer.Provider;
import tripPricer.TripPricer;

@Service
public class TourGuidePricerService {

    private final TripPricer tripPricer;

    public TourGuidePricerService(TripPricer tripPricer) {
        this.tripPricer = tripPricer;
    }

    @Async("asyncExecutor")
    public CompletableFuture<List<Provider>> getPrice(String tripPricerApiKey, UUID userId, int numberOfAdults, int numberOfChildren,
                                int tripDuration, int cumulatativeRewardPoints) {
        return CompletableFuture.completedFuture(tripPricer.getPrice(tripPricerApiKey, userId, numberOfAdults, numberOfChildren, tripDuration, cumulatativeRewardPoints));
    }
    
}
