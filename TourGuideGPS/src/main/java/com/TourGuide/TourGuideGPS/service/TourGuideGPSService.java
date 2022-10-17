package com.TourGuide.TourGuideGPS.service;

import java.util.List;
import java.util.UUID;

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

    public VisitedLocation trackUserLocation(UUID userId) {
		VisitedLocation visitedLocation = gpsUtil.getUserLocation(userId);
		return visitedLocation;
	}

    public List<Attraction> getAttraction() {
        return gpsUtil.getAttractions();
    }
    
}
