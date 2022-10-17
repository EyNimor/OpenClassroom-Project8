package com.TourGuide.TourGuideMain.feignClient;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "RewardClient", url = "http://localhost:8083/reward/")
public interface RewardClient {

    @GetMapping("/getRewardPointsForMultipleAttractions")
    Map<UUID, Integer> getRewardPointsForMultipleAttractions(@RequestParam(value = "attractionsUuids") List<UUID> attractionsIds, @RequestParam(value = "userUuid") String userId);

    @GetMapping("/getAttractionRewardPoints")
    Integer getAttractionRewardPoints(@RequestParam(value = "attractionUuid") UUID attractionId, @RequestParam(value = "userUuid") UUID userId);
    
}
