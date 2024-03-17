package com.example.mosawebapp.kiosk.dto;

public class KioskOrderQuantityForm {
  private String kioskToken;
  private String itemId;

  public KioskOrderQuantityForm(){}

  public KioskOrderQuantityForm(String kioskToken, String itemId) {
    this.kioskToken = kioskToken;
    this.itemId = itemId;
  }

  public String getItemId() {
    return itemId;
  }

  public void setItemId(String itemId) {
    this.itemId = itemId;
  }

  public String getKioskToken() {
    return kioskToken;
  }

  public void setKioskToken(String kioskToken) {
    this.kioskToken = kioskToken;
  }
}
