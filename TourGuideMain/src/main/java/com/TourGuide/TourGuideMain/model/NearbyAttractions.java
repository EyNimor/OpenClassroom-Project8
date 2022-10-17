package com.TourGuide.TourGuideMain.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
@NoArgsConstructor
public class NearbyAttractions {

    public Map<String, Location> attractionsList;
    
    public Location userLocation;

    public Map<String, Double> distances;

    public Map<String, Integer> rewardPoints;

    public NearbyAttractions(List<Attraction> attractionsList, Location userLocation, Map<Attraction, Double> distanceOfEachAttraction, Map<Attraction, Integer> rewardPointsForEachAttraction) throws Exception {
        if(attractionsList.size() > 5) {
            throw new Exception("There is more than 5 attractions in attractionList");
        }
        else if(distanceOfEachAttraction.size() > 5) {
            throw new Exception("There is more than 5 attractions in distanceOfEachAttraction Map");
        }
        else if(rewardPointsForEachAttraction.size() > 5) {
            throw new Exception("There is more than 5 attractions in rewardPointsForEachAttraction Map");
        }
        else {
            this.attractionsList = new HashMap<>();
            this.setUserLocation(userLocation);
            this.distances = new HashMap<>();
            this.rewardPoints = new HashMap<>();
            for(int i = 0 ; i < 5 ; i++) {
                this.attractionsList.put(attractionsList.get(i).getAttractionName(), new Location(attractionsList.get(i).longitude, attractionsList.get(i).latitude));
                this.distances.put(attractionsList.get(i).getAttractionName(), distanceOfEachAttraction.get(attractionsList.get(i)));
                this.rewardPoints.put(attractionsList.get(i).getAttractionName(), rewardPointsForEachAttraction.get(attractionsList.get(i)));
            }
        }
    }

}
