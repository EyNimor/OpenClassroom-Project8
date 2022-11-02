package com.TourGuide.TourGuideGPS.controller;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private Logger logger = LoggerFactory.getLogger(TourGuideGPSController.class);

    @Autowired
    private TourGuideGPSService service;
    
    @RequestMapping(value = "/getUserLocation", method = RequestMethod.GET)
    public VisitedLocation getUserLocation(@RequestParam(value = "uuid") String userId) throws InterruptedException, ExecutionException {
        logger.info("Request getUserLocation for userId : " + userId);
        UUID uuid = UUID.fromString(userId);
        VisitedLocation visitedLocation = service.trackUserLocation(uuid).get();
        return visitedLocation;
    }

    @RequestMapping(value = "/getAllAttractions", method = RequestMethod.GET)
    public List<Attraction> getAllAttractions() throws InterruptedException, ExecutionException {
        logger.info("Request getAllAttractions");
        return service.getAttraction().get();
    }

}
