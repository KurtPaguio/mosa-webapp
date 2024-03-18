package com.example.mosawebapp.kiosk.dto;

public class KioskOrderQuantityForm {
  private String kioskNumber;
  private String itemId;

  public KioskOrderQuantityForm(){}

  public KioskOrderQuantityForm(String kioskNumber, String itemId) {
    this.kioskNumber = kioskNumber;
    this.itemId = itemId;
  }

  public String getItemId() {
    return itemId;
  }

  public void setItemId(String itemId) {
    this.itemId = itemId;
  }

  public String getKioskNumber() {
    return kioskNumber;
  }

  public void setKioskNumber(String kioskNumber) {
    this.kioskNumber = kioskNumber;
  }
}
