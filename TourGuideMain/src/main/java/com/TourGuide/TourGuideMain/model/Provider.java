package com.TourGuide.TourGuideMain.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor @AllArgsConstructor
public class Provider {
  public String name;
  
  public double price;
  
  public String tripId;
}

