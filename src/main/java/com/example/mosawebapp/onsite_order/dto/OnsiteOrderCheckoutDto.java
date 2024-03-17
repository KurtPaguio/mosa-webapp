package com.example.mosawebapp.onsite_order.dto;


import com.example.mosawebapp.onsite_order.domain.OnsiteOrderCheckout;
import com.example.mosawebapp.onsite_order.domain.OnsiteOrderItem;
import com.example.mosawebapp.utils.DateTimeFormatter;
import java.util.List;

public class OnsiteOrderCheckoutDto {
  private String checkoutId;
  private String dateCheckout;
  private OnsiteOrderDto order;

  public OnsiteOrderCheckoutDto(){}

  public OnsiteOrderCheckoutDto(String checkoutId, String dateCheckout, OnsiteOrderDto order) {
    this.checkoutId = checkoutId;
    this.dateCheckout = dateCheckout;
    this.order = order;
  }

  public static OnsiteOrderCheckoutDto buildFromEntity(OnsiteOrderCheckout checkout){
    return new OnsiteOrderCheckoutDto(checkout.getId(), DateTimeFormatter.get_MMDDYYY_Format(checkout.getDateCreated()),
        OnsiteOrderDto.buildFromEntity(checkout.getOrder()));
  }

  public static OnsiteOrderCheckoutDto buildFromEntityV2(OnsiteOrderCheckout checkout, List<OnsiteOrderItem> items){
    return new OnsiteOrderCheckoutDto(checkout.getId(), DateTimeFormatter.get_MMDDYYY_Format(checkout.getDateCreated()),
        new OnsiteOrderDto(checkout.getOrder(), items));
  }

  public String getCheckoutId() {
    return checkoutId;
  }

  public void setCheckoutId(String checkoutId) {
    this.checkoutId = checkoutId;
  }

  public String getDateCheckout() {
    return dateCheckout;
  }

  public void setDateCheckout(String dateCheckout) {
    this.dateCheckout = dateCheckout;
  }

  public OnsiteOrderDto getOrder() {
    return order;
  }

  public void setOrder(OnsiteOrderDto order) {
    this.order = order;
  }
}
