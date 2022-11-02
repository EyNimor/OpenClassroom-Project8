package com.TourGuide.TourGuideMain;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.TourGuide.TourGuideMain.config.AsyncConfiguration;
import com.TourGuide.TourGuideMain.feignClient.GpsClient;
import com.TourGuide.TourGuideMain.helper.InternalTestHelper;
import com.TourGuide.TourGuideMain.model.Attraction;
import com.TourGuide.TourGuideMain.model.User;
import com.TourGuide.TourGuideMain.model.VisitedLocation;
import com.TourGuide.TourGuideMain.service.TourGuideMainService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Import({AsyncConfiguration.class, TourGuideMainService.class})
public class TestPerformance {

	private Logger logger = LoggerFactory.getLogger(TestPerformance.class);
	
	// Users should be incremented up to 100,000
	private Integer numberOfUsers = 100000;

	@Autowired
	private GpsClient gps;

	@Autowired
	private TourGuideMainService service;

	@PostConstruct
	public void init() {
		InternalTestHelper.setInternalUserNumber(numberOfUsers);
		service.initializeInternalUsers();
	}
	
	/*
	 * A note on performance improvements:
	 *     
	 *     The number of users generated for the high volume tests can be easily adjusted via this method:
	 *     
	 *     		InternalTestHelper.setInternalUserNumber(100000);
	 *     
	 *     
	 *     These tests can be modified to suit new solutions, just as long as the performance metrics
	 *     at the end of the tests remains consistent. 
	 * 
	 *     These are performance metrics that we are trying to hit:
	 *     
	 *     highVolumeTrackLocation: 100,000 users within 15 minutes:
	 *     		assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
     *
     *     highVolumeGetRewards: 100,000 users within 20 minutes:
	 *          assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	 */
	
	@Test
	public void highVolumeTrackLocation() throws InterruptedException, ExecutionException {
		//This represent the maximum execution time for each request in millisecond. It's multiply by the number of users to obtain the test objective
		//With 9ms, the objective of the test is to be finished within 15min for 100,000 users
		Integer averageExecutionTimeForEachRequest = 9;

		List<User> allUsers = new ArrayList<>();
		allUsers = service.getAllUsers();

		CompletableFuture<VisitedLocation> asyncCall = null;
	    StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		int i = 0;
		for(User user : allUsers) {
			i = i + 1;
			logger.info("Loop number : " + i);
			asyncCall = service.trackUserLocation(user.getUserName());
		}
		CompletableFuture.allOf(asyncCall).get();
		stopWatch.stop();

		logger.info("highVolumeGetRewards: Time goal : " + TimeUnit.MILLISECONDS.toSeconds(averageExecutionTimeForEachRequest * numberOfUsers) + " seconds.");
		logger.info("highVolumeGetRewards: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds."); 
		assertTrue(TimeUnit.MILLISECONDS.toSeconds(averageExecutionTimeForEachRequest * numberOfUsers) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	}

	@Test
	public void highVolumeGetRewards() throws InterruptedException, ExecutionException {
		//This represent the maximum execution time for each request in millisecond. It's multiply by the number of users to obtain the test objective
		//With 12ms, the objective of the test is to be finished within 20min for 100,000 users
		Integer averageExecutionTimeForEachRequest = 12;

		CompletableFuture<User> asyncCall = null;
		List<CompletableFuture<User>> asyncList = new ArrayList<>();
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		
	    Attraction attraction = gps.getAllAttractions().get(0);
		List<User> allUsers = new ArrayList<>();
		allUsers = service.getAllUsers();
		allUsers.forEach(u -> u.addToVisitedLocations(new VisitedLocation(u.getUserId(), attraction, new Date())));
	     
		int i = 0;
	    for(User user : allUsers) {
			i = i + 1;
			logger.info("Loop number : " + i);
			asyncCall = service.calculateRewards(user);
			asyncList.add(asyncCall);
		}
		CompletableFuture.allOf(asyncCall).get();
	    
		List<User> allUpdatedUsers = new ArrayList<>();
		for(CompletableFuture<User> async : asyncList) {
			allUpdatedUsers.add(async.get());
		}
		stopWatch.stop();
		
		for(User user : allUpdatedUsers) {
			assertTrue(user.getUserRewards().size() > 0);
		}

		logger.info("highVolumeGetRewards: Time goal : " + TimeUnit.MILLISECONDS.toSeconds(averageExecutionTimeForEachRequest * numberOfUsers) + " seconds.");
		logger.info("highVolumeGetRewards: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds."); 
		assertTrue(TimeUnit.MILLISECONDS.toSeconds(averageExecutionTimeForEachRequest * numberOfUsers) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	}
	
}
