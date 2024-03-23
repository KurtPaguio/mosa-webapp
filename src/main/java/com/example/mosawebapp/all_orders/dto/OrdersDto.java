package com.example.mosawebapp.all_orders.dto;

import com.example.mosawebapp.all_orders.domain.OrderType;
import com.example.mosawebapp.all_orders.domain.Orders;
import com.example.mosawebapp.cart.domain.Cart;
import com.example.mosawebapp.cart.dto.CartDto;
import com.example.mosawebapp.utils.DateTimeFormatter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;

@JsonInclude(Include.NON_NULL)
public class OrdersDto {
  private String orderId;
  private String dateOrdered;
  private String paymentMethod;
  private String referenceNumber;
  private OrderType orderType;
  private float orderTotalPrice;
  private List<CartDto> onlineOrders;

  public OrdersDto(){}

  public OrdersDto(String orderId, String dateOrdered, List<CartDto> onlineOrders) {
    this.orderId = orderId;
    this.dateOrdered = dateOrdered;
    this.onlineOrders = onlineOrders;
  }

  public OrdersDto(Orders orders){
    this.orderId = orders.getId();
    this.dateOrdered = DateTimeFormatter.get_MMDDYYY_Format(orders.getDateCreated());
    this.referenceNumber = orders.getReferenceNumber();
    this.orderType = orders.getOrderType();
    this.paymentMethod = orders.getPaymentMethod();
  }

  public OrdersDto(Orders orders, List<CartDto> onlineOrders){
    this.paymentMethod = orders.getPaymentMethod();
    this.orderId = orders.getOrderId();
    this.dateOrdered = DateTimeFormatter.get_MMDDYYY_Format(orders.getDateCreated());
    this.referenceNumber = orders.getReferenceNumber();

    if(onlineOrders != null || !onlineOrders.isEmpty()){
      this.orderType = orders.getOrderType();
      this.onlineOrders = onlineOrders;
      this.orderTotalPrice = computeTotalPriceForOnlineOrders(onlineOrders);
    }
  }

  private float computeTotalPriceForOnlineOrders(List<CartDto> onlineOrders){
    float price = 0;

    for(CartDto dto: onlineOrders){
      price += dto.getTotalPrice();
    }

    return price;
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

  public List<CartDto> getOnlineOrders() {
    return onlineOrders;
  }

  public void setOnlineOrders(List<CartDto> onlineOrders) {
    this.onlineOrders = onlineOrders;
  }

  public OrderType getOrderType() {
    return orderType;
  }

  public void setOrderType(OrderType orderType) {
    this.orderType = orderType;
  }

  public String getReferenceNumber() {
    return referenceNumber;
  }

  public void setReferenceNumber(String referenceNumber) {
    this.referenceNumber = referenceNumber;
  }

  public float getOrderTotalPrice() {
    return orderTotalPrice;
  }

  public void setOrderTotalPrice(float orderTotalPrice) {
    this.orderTotalPrice = orderTotalPrice;
  }

  public String getPaymentMethod() {
    return paymentMethod;
  }

  public void setPaymentMethod(String paymentMethod) {
    this.paymentMethod = paymentMethod;
  }
}
