package com.TourGuide.TourGuideMain.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.TourGuide.TourGuideMain.feignClient.GpsClient;
import com.TourGuide.TourGuideMain.feignClient.PricerClient;
import com.TourGuide.TourGuideMain.feignClient.RewardClient;
import com.TourGuide.TourGuideMain.helper.InternalTestHelper;
import com.TourGuide.TourGuideMain.model.Attraction;
import com.TourGuide.TourGuideMain.model.Location;
import com.TourGuide.TourGuideMain.model.NearbyAttractions;
import com.TourGuide.TourGuideMain.model.Provider;
import com.TourGuide.TourGuideMain.model.User;
import com.TourGuide.TourGuideMain.model.UserReward;
import com.TourGuide.TourGuideMain.model.VisitedLocation;

@Service
public class TourGuideMainService {

	private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

	@Autowired
    private GpsClient gps;

	@Autowired
	private RewardClient reward;

	@Autowired
	private PricerClient pricer;

	//Values in Miles
	private int defaultProximityBuffer = 10;
	private int proximityBuffer = defaultProximityBuffer;
	private int attractionProximityRange = 200;

    private Logger logger = LoggerFactory.getLogger(TourGuideMainService.class);
    private boolean isTestMode = true;

    public TourGuideMainService() {
        if(isTestMode) {
			logger.info("TestMode enabled");
			logger.debug("Initializing users");
			initializeInternalUsers();
			logger.debug("Finished initializing users");
		}
    }

	public void setProximityBuffer(int proximityBuffer) {
		this.proximityBuffer = proximityBuffer;
	}
	
	public void setDefaultProximityBuffer() {
		proximityBuffer = defaultProximityBuffer;
	}

	public User getUser(String userName) {
		return internalUserMap.get(userName);
	}

	public List<User> getAllUsers() {
		return internalUserMap.values().stream().collect(Collectors.toList());
	}

	@Async("asyncExecutor")
	public CompletableFuture<VisitedLocation> trackUserLocation(String userName) {
        logger.info("Getting location of user : " + userName);
		User user = getUser(userName);
        VisitedLocation visitedLocation = gps.getUserLocation(user.getUserId().toString());
        user.addToVisitedLocations(visitedLocation);
        calculateRewards(user);
        return CompletableFuture.completedFuture(visitedLocation);
    }

	@Async("asyncExecutor")
	public CompletableFuture<NearbyAttractions> getNearbyAttractions(String userName) throws Exception {
		try {
			logger.info("Getting nearby Attractions of user : " + userName);
			User user = getUser(userName);
			CompletableFuture<VisitedLocation> userLocationAsyncCall = trackUserLocation(userName);
        	List<Attraction> unfilteredAttractionsList = gps.getAllAttractions();; 

			Location userLocation = userLocationAsyncCall.get().location;
			Map<Attraction, Double> unsortedDistanceOfEachAttraction = new HashMap<>();
			for(int i = 0 ; i < unfilteredAttractionsList.size() ; i++) {
				unsortedDistanceOfEachAttraction.put(unfilteredAttractionsList.get(i), getDistance(new Location(unfilteredAttractionsList.get(i).longitude, unfilteredAttractionsList.get(i).latitude), userLocation));
			}
			Map<Attraction, Double> unfilteredDistanceOfEachAttraction = sortByValue(unsortedDistanceOfEachAttraction);
			Map<Attraction, Double> distanceOfEachAttraction = filter(unfilteredDistanceOfEachAttraction);

			List<Attraction> attractionsList = new ArrayList<>();
			for(int i = 0 ; i < unfilteredAttractionsList.size() ; i++) {
				if(distanceOfEachAttraction.containsKey(unfilteredAttractionsList.get(i))) {
					attractionsList.add(unfilteredAttractionsList.get(i));
				}
			}
			List<UUID> attractionsIds = new ArrayList<>();
			for(int i = 0 ; i < attractionsList.size() ; i++) {
				attractionsIds.add(attractionsList.get(i).getAttractionId());
			}

			CompletableFuture<Map<UUID, Integer>> rewardsAsyncCall = CompletableFuture.supplyAsync(() -> reward.getRewardPointsForMultipleAttractions(attractionsIds, user.getUserId().toString()));
			Map<UUID, Integer> rewardsPoints = rewardsAsyncCall.get();
        	Map<Attraction, Integer> rewardPointsForEachAttraction = new HashMap<>();
			for(int i = 0 ; i < attractionsList.size() ; i++) {
				rewardPointsForEachAttraction.put(attractionsList.get(i), rewardsPoints.get(attractionsList.get(i).getAttractionId()));
			}

        	NearbyAttractions nearbyAttractions = new NearbyAttractions(attractionsList, userLocation, distanceOfEachAttraction, rewardPointsForEachAttraction);
			return CompletableFuture.completedFuture(nearbyAttractions);
		}
		catch(Exception e) {
			e.fillInStackTrace();
			return null;
		}
	}

	private static Map<Attraction, Double> sortByValue(Map<Attraction, Double> unsortMap) {

        // 1. Convert Map to List of Map
        List<Map.Entry<Attraction, Double>> list =
                new LinkedList<Map.Entry<Attraction, Double>>(unsortMap.entrySet());

        // 2. Sort list with Collections.sort(), provide a custom Comparator
        //    Try switch the o1 o2 position for a different order
        Collections.sort(list, new Comparator<Map.Entry<Attraction, Double>>() {
            public int compare(Map.Entry<Attraction, Double> o1,
                               Map.Entry<Attraction, Double> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        // 3. Loop the sorted list and put it into a new insertion order Map LinkedHashMap
        Map<Attraction, Double> sortedMap = new LinkedHashMap<Attraction, Double>();
        for (Map.Entry<Attraction, Double> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

	private static Map<Attraction, Double> filter(Map<Attraction, Double> mapToFilter) {
		List<Map.Entry<Attraction, Double>> list =
                new LinkedList<Map.Entry<Attraction, Double>>(mapToFilter.entrySet());

		int index = 0;
		Map<Attraction, Double> filteredMap = new LinkedHashMap<Attraction, Double>();
		for (Map.Entry<Attraction, Double> entry : list) {
			if(index < 5) {
				filteredMap.put(entry.getKey(), entry.getValue());
			}
			else {
				break;
			}
			index = index + 1;
		}

		return filteredMap;
	}

	public List<UserReward> getUserRewards(User user) {
		return user.getUserRewards();
	}

	@Async("asyncExecutor")
	public CompletableFuture<Map<String, Location>> getAllUsersCurentLocations() {
		List<User> users = getAllUsers();
		Map<String, Location> map = new HashMap<>();
		for(User user : users) {
			map.put(user.getUserId().toString(), user.getLastVisitedLocation().getLocation());
		}
		return CompletableFuture.completedFuture(map);
    }

	@Async("asyncExecutor")
	public CompletableFuture<List<Provider>> getTripDeals(String userName) {
		User user = getUser(userName);
        int cumulatativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();
		List<Provider> providers = pricer.getPrice(tripPricerApiKey, user.getUserId(), user.getUserPreferences().getNumberOfAdults(), 
				user.getUserPreferences().getNumberOfChildren(), user.getUserPreferences().getTripDuration(), cumulatativeRewardPoints);
		user.setTripDeals(providers);
		return CompletableFuture.completedFuture(providers);
    }

	/**********************************************************************************
	 * 
	 * Methods from RewardService (Maybe will be moved to another class ?)
	 * 
	 **********************************************************************************/

	@Async("asyncExecutor")
	public CompletableFuture<User> calculateRewards(User user) {
		logger.info("Calculating Reward for user : " + user.getUserName());
		List<VisitedLocation> userLocations = user.getVisitedLocations();
		List<Attraction> attractions = gps.getAllAttractions();
		
		for(VisitedLocation visitedLocation : userLocations) {
			for(Attraction attraction : attractions) {
				if(user.getUserRewards().stream().filter(r -> r.attraction.attractionName.equals(attraction.attractionName)).count() == 0) {
					if(nearAttraction(visitedLocation, attraction)) {
						user.addUserReward(new UserReward(visitedLocation, attraction, reward.getAttractionRewardPoints(attraction.attractionId, user.getUserId())));
					}
				}
			}
		}

		return CompletableFuture.completedFuture(user);
	}

	public boolean isWithinAttractionProximity(Attraction attraction, Location location) {
		return getDistance(attraction, location) > attractionProximityRange ? false : true;
	}
	
	private boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
		return getDistance(attraction, visitedLocation.location) > proximityBuffer ? false : true;
	}

	public double getDistance(Location loc1, Location loc2) {
        double lat1 = Math.toRadians(loc1.latitude);
        double lon1 = Math.toRadians(loc1.longitude);
        double lat2 = Math.toRadians(loc2.latitude);
        double lon2 = Math.toRadians(loc2.longitude);

        double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                               + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        double nauticalMiles = 60 * Math.toDegrees(angle);
        double statuteMiles = STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
        return statuteMiles;
	}
    
    /**********************************************************************************
	 * 
	 * Internal Testing Methods
	 * 
	 **********************************************************************************/

	private static final String tripPricerApiKey = "test-server-api-key";

    private Map<String, User> internalUserMap = new HashMap<>();
    
	public void initializeInternalUsers() {
		if(internalUserMap.size() > 0) {
			internalUserMap = new HashMap<>();
		}
		IntStream.range(0, InternalTestHelper.getInternalUserNumber()).forEach(i -> {
			String userName = "internalUser" + i;
			String phone = "000";
			String email = userName + "@tourGuide.com";
			User user = new User(UUID.randomUUID(), userName, phone, email);
			generateUserLocationHistory(user);
			
			internalUserMap.put(userName, user);
		});
		logger.debug("Created " + InternalTestHelper.getInternalUserNumber() + " internal test users.");
	}

    private void generateUserLocationHistory(User user) {
		IntStream.range(0, 3).forEach(i-> {
			user.addToVisitedLocations(new VisitedLocation(user.getUserId(), new Location(generateRandomLatitude(), generateRandomLongitude()), getRandomTime()));
		});
	}
	
	private double generateRandomLongitude() {
		double leftLimit = -180;
	    double rightLimit = 180;
	    return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
	}
	
	private double generateRandomLatitude() {
		double leftLimit = -85.05112878;
	    double rightLimit = 85.05112878;
	    return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
	}
	
	private Date getRandomTime() {
		LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
	    return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
	}

}
