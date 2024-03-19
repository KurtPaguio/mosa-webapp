package com.example.mosawebapp.all_orders.dto;

import com.example.mosawebapp.all_orders.domain.OrderType;
import com.example.mosawebapp.all_orders.domain.Orders;
import com.example.mosawebapp.cart.domain.Cart;
import com.example.mosawebapp.cart.dto.CartDto;
import com.example.mosawebapp.utils.DateTimeFormatter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class OrdersDto {
  private String orderId;
  private String dateOrdered;
  private OrderType orderType;
  private CartDto onlineOrders;

  public OrdersDto(){}

  public OrdersDto(String orderId, String dateOrdered, CartDto onlineOrders) {
    this.orderId = orderId;
    this.dateOrdered = dateOrdered;
    this.onlineOrders = onlineOrders;
  }

  public OrdersDto(Orders orders, Cart onlineOrders){
    this.orderId = orders.getId();
    this.dateOrdered = DateTimeFormatter.get_MMDDYYY_Format(orders.getDateCreated());

    if(onlineOrders != null){
      this.orderType = orders.getOrderType();
      this.onlineOrders = new CartDto(onlineOrders);
    }
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

  public CartDto getOnlineOrders() {
    return onlineOrders;
  }

  public void setOnlineOrders(CartDto onlineOrders) {
    this.onlineOrders = onlineOrders;
  }

  public OrderType getOrderType() {
    return orderType;
  }

  public void setOrderType(OrderType orderType) {
    this.orderType = orderType;
  }
}
