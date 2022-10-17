package com.TourGuide.TourGuideMain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter @Setter @ToString
@AllArgsConstructor
public class UserReward {

	public VisitedLocation visitedLocation;
	public Attraction attraction;
	private int rewardPoints;
	
	public UserReward(VisitedLocation visitedLocation, Attraction attraction) {
		this.visitedLocation = visitedLocation;
		this.attraction = attraction;
	}
	
}
