package com.TourGuide.TourGuideMain.feignClient;

import java.util.List;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.TourGuide.TourGuideMain.config.FeignConfiguration;
import com.TourGuide.TourGuideMain.model.Provider;

@FeignClient(name = "PricerClient", url = "http://localhost:8082/pricer/", configuration = FeignConfiguration.class)
public interface PricerClient {

    @GetMapping("/getPrice")
    List<Provider> getPrice(@RequestParam(value = "apiKey") String tripPricerApiKey, @RequestParam(value = "uuid") UUID userId, @RequestParam(value = "adults") int numberOfAdults, @RequestParam(value = "childrens") int numberOfChildren,
                        @RequestParam(value = "tripDuration") int tripDuration, @RequestParam(value = "rewardPoints") int cumulatativeRewardPoints);
    
}
