package com.TourGuide.TourGuidePricer.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.TourGuide.TourGuidePricer.service.TourGuidePricerService;

import tripPricer.Provider;

@RestController
@RequestMapping(value = "/pricer")
public class TourGuidePricerController {

    @Autowired
    TourGuidePricerService service;

    @RequestMapping(value = "/getPrice", method = RequestMethod.GET)
    public List<Provider> getPrice(@RequestParam(value = "apiKey") String tripPricerApiKey, @RequestParam(value = "uuid") UUID userId, @RequestParam(value = "adults") int numberOfAdults, @RequestParam(value = "childrens") int numberOfChildren,
                                @RequestParam(value = "tripDuration") int tripDuration, @RequestParam(value = "rewardPoints") int cumulatativeRewardPoints) {
        return service.getPrice(tripPricerApiKey, userId, numberOfAdults, numberOfChildren, tripDuration, cumulatativeRewardPoints);
    }
    
}
