package com.TourGuide.TourGuideMain.model;

import java.util.Date;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString @Getter
@NoArgsConstructor @AllArgsConstructor
public class VisitedLocation {

  public UUID userId;
  
  public Location location;
  
  public Date timeVisited;

}