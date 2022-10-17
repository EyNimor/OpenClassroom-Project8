package com.TourGuide.TourGuideMain.feignClient;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.TourGuide.TourGuideMain.model.Attraction;
import com.TourGuide.TourGuideMain.model.VisitedLocation;

@FeignClient(name = "GpsClient", url = "http://localhost:8081/gps/")
public interface GpsClient {

    @GetMapping("/getUserLocation")
    VisitedLocation getUserLocation(@RequestParam(value = "uuid") String userId);

    @GetMapping("/getAllAttraction")
    List<Attraction> getAllAttraction();

}
