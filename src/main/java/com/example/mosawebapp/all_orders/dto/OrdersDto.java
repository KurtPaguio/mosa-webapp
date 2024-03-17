package com.example.mosawebapp.all_orders.dto;

import com.example.mosawebapp.all_orders.domain.OrderType;
import com.example.mosawebapp.cart.dto.CartDto;
import com.example.mosawebapp.kiosk.dto.KioskDto;
import com.example.mosawebapp.onsite_order.dto.OnsiteOrderDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class OrdersDto {
  private String orderId;
  private OrderType orderType;
  private CartDto cart;
  private KioskDto kiosk;
  private OnsiteOrderDto onsiteOrder;

  public OrdersDto(){}

  public OrdersDto(String orderId, CartDto cart, KioskDto kiosk, OnsiteOrderDto onsiteOrder) {
    this.orderId = orderId;

    if(cart != null) {
      this.orderType = OrderType.ONLINE;
      this.cart = cart;
    }

    if(kiosk != null) {
      this.orderType = OrderType.KIOSK;
      this.kiosk = kiosk;
    }

    if(onsiteOrder != null) {
      this.orderType = OrderType.ONSITE;
      this.onsiteOrder = onsiteOrder;
    }
  }

  public String getOrderId() {
    return orderId;
  }

  public void setOrderId(String orderId) {
    this.orderId = orderId;
  }

  public CartDto getCart() {
    return cart;
  }

  public void setCart(CartDto cart) {
    this.cart = cart;
  }

  public KioskDto getKiosk() {
    return kiosk;
  }

  public void setKiosk(KioskDto kiosk) {
    this.kiosk = kiosk;
  }

  public OnsiteOrderDto getOnsiteOrder() {
    return onsiteOrder;
  }

  public void setOnsiteOrder(OnsiteOrderDto onsiteOrder) {
    this.onsiteOrder = onsiteOrder;
  }

  public OrderType getOrderType() {
    return orderType;
  }

  public void setOrderType(OrderType orderType) {
    this.orderType = orderType;
  }
}
