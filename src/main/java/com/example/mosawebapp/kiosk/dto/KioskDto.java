package com.example.mosawebapp.kiosk.dto;

import com.example.mosawebapp.kiosk.domain.Kiosk;
import com.example.mosawebapp.kiosk.domain.KioskOrder;
import com.example.mosawebapp.utils.DateTimeFormatter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;

@JsonInclude(Include.NON_NULL)
public class KioskDto {
  private String kioskId;
  private String dateCreated;
  private String kioskNumber;
  private List<KioskOrderDto> kioskOrders;
  private boolean isActive;
  private float totalPrice;

  public KioskDto(){}

  public KioskDto(String kioskId, String dateCreated, String kioskNumber, boolean isActive) {
    this.kioskId = kioskId;
    this.dateCreated = dateCreated;
    this.kioskNumber = kioskNumber;
    this.isActive = isActive;
  }

  public KioskDto(String kioskId, String dateCreated, String kioskNumber, List<KioskOrderDto> kioskOrders,
      boolean isActive) {
    this.kioskId = kioskId;
    this.dateCreated = dateCreated;
    this.kioskNumber = kioskNumber;
    this.kioskOrders = kioskOrders;
    this.isActive = isActive;
  }

  public KioskDto(Kiosk kiosk, List<KioskOrder> orders){
    this.kioskId = kiosk.getId();
    this.dateCreated = DateTimeFormatter.get_MMDDYYY_Format(kiosk.getDateCreated());
    this.kioskNumber = kiosk.getKioskNumber();
    this.kioskOrders = KioskOrderDto.buildFromEntitiesV2(orders);
    this.isActive = kiosk.isActive();
    this.totalPrice = getTotalOrderPrice(orders);
  }

  public float getTotalOrderPrice(List<KioskOrder> orders){
    List<KioskOrderDto> dtos = KioskOrderDto.buildFromEntitiesV2(orders);
    float price = 0;

    for(KioskOrderDto order: dtos){
      price += order.getPrice();
    }

    return price;
  }

  public static KioskDto buildFromEntity(Kiosk kiosk){
    return new KioskDto(kiosk.getId(), DateTimeFormatter.get_MMDDYYY_Format(kiosk.getDateCreated()), kiosk.getKioskNumber(), kiosk.isActive());
  }

  public String getKioskId() {
    return kioskId;
  }

  public void setKioskId(String kioskId) {
    this.kioskId = kioskId;
  }

  public String getDateCreated() {
    return dateCreated;
  }

  public void setDateCreated(String dateCreated) {
    this.dateCreated = dateCreated;
  }

  public String getKioskNumber() {
    return kioskNumber;
  }

  public void setKioskNumber(String kioskNumber) {
    this.kioskNumber = kioskNumber;
  }

  public List<KioskOrderDto> getKioskOrders() {
    return kioskOrders;
  }

  public void setKioskOrders(List<KioskOrderDto> kioskOrders) {
    this.kioskOrders = kioskOrders;
  }

  public boolean isActive() {
    return isActive;
  }

  public void setActive(boolean active) {
    isActive = active;
  }

  public float getTotalPrice() {
    return totalPrice;
  }

  public void setTotalPrice(float totalPrice) {
    this.totalPrice = totalPrice;
  }
}
