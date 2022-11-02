package com.TourGuide.TourGuideReward.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.TourGuide.TourGuideReward.service.TourGuideRewardService;

@RestController
@RequestMapping(value = "/reward")
public class TourGuideRewardController {

    private Logger logger = LoggerFactory.getLogger(TourGuideRewardController.class);

    @Autowired
    private TourGuideRewardService service;

    @RequestMapping(value = "/getRewardPointsForMultipleAttractions", method = RequestMethod.GET)
    public Map<UUID, Integer> getRewardPointsForMultipleAttractions(@RequestParam(value = "attractionsUuids") List<UUID> attractionsIds, @RequestParam(value = "userUuid") UUID userId) throws InterruptedException, ExecutionException {
        logger.info("Request getRewardPointsForMultipleAttractions for userId : " + userId + " / for attractionsIds : " + attractionsIds);
        Map<UUID, Integer> rewardPointsForEachAttractions = new HashMap<>();

        for(int i = 0 ; i < attractionsIds.size() ; i++) {
            UUID attractionId = attractionsIds.get(i);
            rewardPointsForEachAttractions.put(attractionId, service.getRewardPoints(attractionId, userId).get());
        }

        return rewardPointsForEachAttractions;
    }

    @RequestMapping(value = "/getAttractionRewardPoints", method = RequestMethod.GET)
    public Integer getAttractionRewardPoints(@RequestParam(value = "attractionUuid") UUID attractionId, @RequestParam(value = "userUuid") UUID userId) throws InterruptedException, ExecutionException { 
        logger.info("Request getAttractionRewardPoints for userId : " + userId + " / for attractionsIds : " + attractionId);
        return service.getRewardPoints(attractionId, userId).get();
    }
    
}
