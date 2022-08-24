package com.TourGuide.TourGuideMain.controller;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.TourGuide.TourGuideMain.model.User;
import com.TourGuide.TourGuideMain.service.TourGuideMainService;
import com.jsoniter.output.JsonStream;

@RestController
@RequestMapping(value = "/tourguide")
public class TourGuideMainController {

    private Logger logger = LoggerFactory.getLogger(TourGuideMainController.class);
    HttpClient client;

    @Autowired
    TourGuideMainService service;

    @PostConstruct
    public void htpClientInit() {
        client = HttpClient.newHttpClient();
    }

    @RequestMapping(value = "/")
    public String index() {
        return "Greetings from TourGuide!";
    }
    
    @RequestMapping("/getLocation") 
    public String getLocation(@RequestParam(value = "userName") String userName) throws IOException, InterruptedException {
        //TODO: Will be operated by TourGuideGPS Microservice - need to be called
        String location;
        User user = service.getUser(userName);
        if(!service.isTrackingNeeded(user.getUserId())) {
            location = JsonStream.serialize(user.getLastVisitedLocation());
        } else {
            HttpRequest request = HttpRequest.newBuilder()
                                    .uri(URI.create("http://localhost:8081/gps/getUserLocation?uuid=" + user.getUserId().toString()))
                                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            location = response.body();
        }
    	return location;
    }
    
    //  TODO: Change this method to no longer return a List of Attractions.
 	//  Instead: Get the closest five tourist attractions to the user - no matter how far away they are.
 	//  Return a new JSON object that contains:
    // Name of Tourist attraction, 
    // Tourist attractions lat/long, 
    // The user's location lat/long, 
    // The distance in miles between the user's location and each of the attractions.
    // The reward points for visiting each Attraction.
    //    Note: Attraction reward points can be gathered from RewardsCentral
    @RequestMapping("/getNearbyAttractions") 
    public String getNearbyAttractions(@RequestParam String userName) {
        //TODO: Will be operated by TourGuideGPS Microservice - need to be called
    	return null;
    }
    
    @RequestMapping("/getRewards") 
    public String getRewards(@RequestParam String userName) {
        //TODO: Will be operated by TourGuideReward Microservice - need to be called
    	return null;
    }
    
    // TODO: Get a list of every user's most recent location as JSON
    //- Note: does not use gpsUtil to query for their current location, 
    //        but rather gathers the user's current location from their stored location history.
    //
    // Return object should be the just a JSON mapping of userId to Locations similar to:
    //     {
    //        "019b04a9-067a-4c76-8817-ee75088c3822": {"longitude":-48.188821,"latitude":74.84371} 
    //        ...
    //     }
    @RequestMapping("/getAllCurrentLocations")
    public String getAllCurrentLocations() {
    	return null;
    }
    
    @RequestMapping("/getTripDeals")
    public String getTripDeals(@RequestParam String userName) {
        //TODO: Will be operated by TourGuidePricer Microservice - need to be called
    	return null;
    }
    
}
