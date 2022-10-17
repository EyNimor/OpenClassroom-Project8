package com.TourGuide.TourGuideGPS.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.TourGuide.TourGuideGPS.service.TourGuideGPSService;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;

@RestController
@RequestMapping(value = "/gps")
public class TourGuideGPSController {

    @Autowired
    TourGuideGPSService service;
    
    @RequestMapping(value = "/getUserLocation", method = RequestMethod.GET)
    public VisitedLocation getUserLocation(@RequestParam(value = "uuid") String userId) {
        UUID uuid = UUID.fromString(userId);
        VisitedLocation visitedLocation = service.trackUserLocation(uuid);
        return visitedLocation;
    }

    @RequestMapping(value = "/getAllAttraction", method = RequestMethod.GET)
    public List<Attraction> getAllAttraction() {
        return service.getAttraction();
    }

}
