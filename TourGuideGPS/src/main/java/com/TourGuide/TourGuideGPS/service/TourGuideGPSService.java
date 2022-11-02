package com.TourGuide.TourGuideGPS.service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;

@Service
public class TourGuideGPSService {

    private final GpsUtil gpsUtil;

    public TourGuideGPSService(GpsUtil gpsUtil) {
        this.gpsUtil = gpsUtil;
    }

    @Async("asyncExecutor")
    public CompletableFuture<VisitedLocation> trackUserLocation(UUID userId) {
		VisitedLocation visitedLocation = gpsUtil.getUserLocation(userId);
		return CompletableFuture.completedFuture(visitedLocation);
	}

    @Async("asyncExecutor")
    public CompletableFuture<List<Attraction>> getAttraction() {
        return CompletableFuture.completedFuture(gpsUtil.getAttractions());
    }
    
}
