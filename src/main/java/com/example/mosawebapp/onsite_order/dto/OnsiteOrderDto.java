package com.example.mosawebapp.onsite_order.dto;

import com.example.mosawebapp.onsite_order.domain.OnsiteOrder;
import com.example.mosawebapp.onsite_order.domain.OnsiteOrderItem;
import com.example.mosawebapp.utils.DateTimeFormatter;
import java.util.List;

public class OnsiteOrderDto {
  private String orderId;
  private String dateOrdered;
  private List<OnsiteOrderItemDto> orderItems;
  private float totalPrice;
  private boolean isOrderActive;

  public OnsiteOrderDto(){}

  public OnsiteOrderDto(String orderId, String dateOrdered, boolean isOrderActive) {
    this.orderId = orderId;
    this.dateOrdered = dateOrdered;
    this.isOrderActive = isOrderActive;
  }

  public OnsiteOrderDto(OnsiteOrder order, List<OnsiteOrderItem> orderItems){
    this.orderId = order.getId();
    this.dateOrdered = DateTimeFormatter.get_MMDDYYY_Format(order.getDateCreated());
    this.orderItems = OnsiteOrderItemDto.buildFromEntitiesV2(orderItems);
    this.isOrderActive = order.isActive();
    this.totalPrice = getTotalCartPrice(orderItems);
  }

  public float getTotalCartPrice(List<OnsiteOrderItem> orderItems){
    List<OnsiteOrderItemDto> dtos = OnsiteOrderItemDto.buildFromEntitiesV2(orderItems);
    float price = 0;

    for(OnsiteOrderItemDto item: dtos){
      price += item.getPrice();
    }

    return price;
  }

  public static OnsiteOrderDto buildFromEntity(OnsiteOrder order){
    return new OnsiteOrderDto(order.getId(), DateTimeFormatter.get_MMDDYYY_Format(order.getDateCreated()), order.isActive());
  }

  public String getOrderId() {
    return orderId;
  }

  public void setOrderId(String orderId) {
    this.orderId = orderId;
  }

  public String getDateOrdered() {
    return dateOrdered;
  }

  public void setDateOrdered(String dateOrdered) {
    this.dateOrdered = dateOrdered;
  }

  public List<OnsiteOrderItemDto> getOrderItems() {
    return orderItems;
  }

  public void setOrderItems(
      List<OnsiteOrderItemDto> orderItems) {
    this.orderItems = orderItems;
  }

  public float getTotalPrice() {
    return totalPrice;
  }

  public void setTotalPrice(float totalPrice) {
    this.totalPrice = totalPrice;
  }

  public boolean isOrderActive() {
    return isOrderActive;
  }

  public void setOrderActive(boolean orderActive) {
    isOrderActive = orderActive;
  }
}
