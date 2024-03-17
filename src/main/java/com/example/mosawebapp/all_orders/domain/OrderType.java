package com.example.mosawebapp.all_orders.domain;

import java.util.Arrays;
import java.util.List;

public enum OrderType {
  ONLINE("Online Order"),
  ONSITE("Onsite Order"),
  KIOSK("Kiosk Order");

  String label;

  OrderType(String label){this.label = label;}

  public static List<OrderType> getTypesAsList(){
    return Arrays.asList(ONLINE, ONSITE, KIOSK);
  }

  public static List<String> getTypesLabelAsList(){
    return Arrays.asList("Online Order", "Onsite Order", "Kiosk Order");
  }
}
