package com.example.mosawebapp.onsite_order.dto;

import com.example.mosawebapp.onsite_order.domain.OnsiteOrderItem;
import com.example.mosawebapp.product.threadtypedetails.domain.ThreadTypeDetails;
import com.example.mosawebapp.product.threadtypedetails.dto.ThreadTypeDetailsDto;
import com.example.mosawebapp.utils.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class OnsiteOrderItemDto {
  private String itemId;
  private String dateAdded;
  private String brandName;
  private String threadType;
  private ThreadTypeDetailsDto details;
  private long quantity;
  private float price;

  public OnsiteOrderItemDto(){}

  public OnsiteOrderItemDto(String itemId, String dateAdded, long quantity, long price) {
    this.itemId = itemId;
    this.dateAdded = dateAdded;
    this.quantity = quantity;
    this.price = price;
  }

  public OnsiteOrderItemDto(OnsiteOrderItem item, ThreadTypeDetails details){
    this.itemId = item.getId();
    this.dateAdded = DateTimeFormatter.get_MMDDYYY_Format(item.getDateCreated());
    this.brandName = item.getType().getBrand().getName();
    this.threadType = item.getType().getType();
    this.details = new ThreadTypeDetailsDto(item.getType(), details);
    this.quantity = item.getQuantity();
    this.price = item.getQuantity() * details.getPrice();
  }

  public static OnsiteOrderItemDto buildFromEntity(OnsiteOrderItem item){
    return new OnsiteOrderItemDto(item.getId(), DateTimeFormatter.get_MMDDYYY_Format(item.getDateCreated()), item.getQuantity(), 0);
  }

  public static List<OnsiteOrderItemDto> buildFromEntities(List<OnsiteOrderItem> items){
    List<OnsiteOrderItemDto> dto = new ArrayList<>();

    for(OnsiteOrderItem item: items){
      dto.add(buildFromEntity(item));
    }

    return dto;
  }

  public static List<OnsiteOrderItemDto> buildFromEntitiesV2(List<OnsiteOrderItem> items){
    List<OnsiteOrderItemDto> dto = new ArrayList<>();

    for(OnsiteOrderItem item: items){
      dto.add(new OnsiteOrderItemDto(item, item.getDetails()));
    }

    return dto;
  }

  public String getItemId() {
    return itemId;
  }

  public void setItemId(String itemId) {
    this.itemId = itemId;
  }

  public String getDateAdded() {
    return dateAdded;
  }

  public void setDateAdded(String dateAdded) {
    this.dateAdded = dateAdded;
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

  public long getQuantity() {
    return quantity;
  }

  public void setQuantity(long quantity) {
    this.quantity = quantity;
  }

  public float getPrice() {
    return price;
  }

  public void setPrice(float price) {
    this.price = price;
  }
}
