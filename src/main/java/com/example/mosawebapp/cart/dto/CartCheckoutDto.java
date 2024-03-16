package com.example.mosawebapp.cart.dto;

import com.example.mosawebapp.cart.domain.CartCheckout;
import com.example.mosawebapp.cart.domain.CartItem;
import com.example.mosawebapp.utils.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CartCheckoutDto {
  private String checkoutId;
  private String dateCheckout;
  private CartDto cart;

  public CartCheckoutDto(){}

  public CartCheckoutDto(String checkoutId, String dateCheckout,
      CartDto cart) {
    this.checkoutId = checkoutId;
    this.dateCheckout = dateCheckout;
    this.cart = cart;
  }

  public static CartCheckoutDto buildFromEntity(CartCheckout checkout){
    return new CartCheckoutDto(checkout.getId(), DateTimeFormatter.get_MMDDYYY_Format(checkout.getDateCreated()), CartDto.buildFromEntity(checkout.getCart()));
  }

  public static CartCheckoutDto buildFromEntityV2(CartCheckout checkout, List<CartItem> cartItems){
    return new CartCheckoutDto(checkout.getId(), DateTimeFormatter.get_MMDDYYY_Format(checkout.getDateCreated()),
        new CartDto(checkout.getCart(), checkout.getAccount(), cartItems));
  }

  public static List<CartCheckoutDto> buildFromEntities(List<CartCheckout> checkouts){
    List<CartCheckoutDto> dto = new ArrayList<>();

    for(CartCheckout checkout: checkouts){
      dto.add(buildFromEntity(checkout));
    }

    return dto;
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

  public CartDto getCart() {
    return cart;
  }

  public void setCart(CartDto cart) {
    this.cart = cart;
  }
}
