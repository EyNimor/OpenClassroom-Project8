package com.TourGuide.TourGuideMain.model;

import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter @ToString
@NoArgsConstructor
public class Attraction extends Location {
  public String attractionName;
  
  public String city;
  
  public String state;
  
  public UUID attractionId;
  
  public Attraction(String attractionName, String city, String state, double latitude, double longitude) {
    super(latitude, longitude);
    this.attractionName = attractionName;
    this.city = city;
    this.state = state;
    this.attractionId = UUID.randomUUID();
  }
}
