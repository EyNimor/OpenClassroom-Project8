package com.TourGuide.TourGuideMain.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.TourGuide.TourGuideMain.model.Location;
import com.TourGuide.TourGuideMain.model.NearbyAttractions;
import com.TourGuide.TourGuideMain.model.Provider;
import com.TourGuide.TourGuideMain.model.VisitedLocation;
import com.TourGuide.TourGuideMain.service.TourGuideMainService;
import com.jsoniter.output.JsonStream;

@RestController
@RequestMapping(value = "/tourguide")
public class TourGuideMainController {

    @Autowired
    TourGuideMainService service;

    @RequestMapping(value = "/")
    public String index() {
        return "Greetings from TourGuide!";
    }
    
    @RequestMapping("/getLocation") 
    public String getLocation(@RequestParam(value = "userName") String userName) throws IOException, InterruptedException, ExecutionException {
        CompletableFuture<VisitedLocation> asyncCall = service.trackUserLocation(userName);
    	return JsonStream.serialize(asyncCall.get().location);
    }

    @RequestMapping("/getNearbyAttractions") 
    public String getNearbyAttractions(@RequestParam(value = "userName") String userName) throws Exception {
        CompletableFuture<NearbyAttractions> asyncCall = service.getNearbyAttractions(userName);
    	return JsonStream.serialize(asyncCall.get());
    }
    
    @RequestMapping("/getRewards") 
    public String getRewards(@RequestParam(value = "userName") String userName) {
    	return JsonStream.serialize(service.getUserRewards(service.getUser(userName)));
    }

    @RequestMapping("/getAllCurrentLocations")
    public String getAllCurrentLocations() throws InterruptedException, ExecutionException {
        CompletableFuture<Map<String, Location>> asyncCall = service.getAllUsersCurentLocations();
    	return JsonStream.serialize(asyncCall.get());
    }
    
    @RequestMapping("/getTripDeals")
    public String getTripDeals(@RequestParam(value = "userName") String userName) throws InterruptedException, ExecutionException {
        CompletableFuture<List<Provider>> asyncCall = service.getTripDeals(userName);
    	return JsonStream.serialize(asyncCall.get());
    }
    
}
