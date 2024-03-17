package com.example.mosawebapp.kiosk.dto;

import com.example.mosawebapp.kiosk.domain.Kiosk;
import com.example.mosawebapp.kiosk.domain.KioskOrder;
import com.example.mosawebapp.product.threadtypedetails.domain.ThreadTypeDetails;
import com.example.mosawebapp.product.threadtypedetails.dto.ThreadTypeDetailsDto;
import com.example.mosawebapp.utils.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class KioskOrderDto {
  private String kioskOrderId;
  private String dateCreated;
  private String brandName;
  private String threadType;
  private ThreadTypeDetailsDto details;
  private float price;
  private long quantity;

  public KioskOrderDto(){}

  public KioskOrderDto(String kioskOrderId, String dateCreated, long quantity) {
    this.kioskOrderId = kioskOrderId;
    this.dateCreated = dateCreated;
    this.quantity = quantity;
  }

  public KioskOrderDto(KioskOrder order, ThreadTypeDetails details) {
    this.kioskOrderId = order.getId();
    this.dateCreated = DateTimeFormatter.get_MMDDYYY_Format(order.getDateCreated());
    this.brandName = order.getType().getBrand().getName();
    this.threadType = order.getType().getType();
    this.details = new ThreadTypeDetailsDto(order.getType(), details);
    this.price = order.getQuantity() * details.getPrice();
    this.quantity = order.getQuantity();
  }

  public static KioskOrderDto buildFromEntity(KioskOrder order){
    return new KioskOrderDto(order.getId(), DateTimeFormatter.get_MMDDYYY_Format(order.getDateCreated()), order.getQuantity());
  }

  public static List<KioskOrderDto> buildFromEntitiesV2(List<KioskOrder> orders){
    List<KioskOrderDto> dto = new ArrayList<>();

    for(KioskOrder order: orders){
      dto.add(new KioskOrderDto(order, order.getDetails()));
    }

    return dto;
  }

  public String getKioskOrderId() {
    return kioskOrderId;
  }

  public void setKioskOrderId(String kioskOrderId) {
    this.kioskOrderId = kioskOrderId;
  }

  public String getDateCreated() {
    return dateCreated;
  }

  public void setDateCreated(String dateCreated) {
    this.dateCreated = dateCreated;
  }

  public String getBrandName() {
    return brandName;
  }

  public void setBrandName(String brandName) {
    this.brandName = brandName;
  }

  public String getThreadType() {
    return threadType;
  }

  public void setThreadType(String threadType) {
    this.threadType = threadType;
  }

  public ThreadTypeDetailsDto getDetails() {
    return details;
  }

  public void setDetails(ThreadTypeDetailsDto details) {
    this.details = details;
  }

  public float getPrice() {
    return price;
  }

  public void setPrice(float price) {
    this.price = price;
  }

  public long getQuantity() {
    return quantity;
  }

  public void setQuantity(long quantity) {
    this.quantity = quantity;
  }
}
