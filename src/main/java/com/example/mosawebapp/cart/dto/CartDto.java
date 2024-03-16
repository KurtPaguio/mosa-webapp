package com.example.mosawebapp.cart.dto;

import com.example.mosawebapp.account.domain.Account;
import com.example.mosawebapp.account.dto.AccountDto;
import com.example.mosawebapp.cart.domain.Cart;
import com.example.mosawebapp.cart.domain.CartItem;
import com.example.mosawebapp.utils.DateTimeFormatter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;

@JsonInclude(Include.NON_NULL)
public class CartDto {
  private String cartId;
  private String dateCreated;
  private AccountDto customer;
  private List<CartItemDto> cartItems;
  private long totalPrice;
  private boolean isCartActive;

  public CartDto(){}

  public CartDto(String cartId, String dateCreated, Account customer, boolean isCartActive) {
    this.cartId = cartId;
    this.dateCreated = dateCreated;
    this.customer = AccountDto.buildFromEntity(customer);
    this.isCartActive = isCartActive;
  }

  public CartDto(Cart cart, Account account, List<CartItem> cartItems){
    this.cartId = cart.getId();
    this.dateCreated = DateTimeFormatter.get_MMDDYYY_Format(cart.getDateCreated());
    this.customer = AccountDto.buildFromEntity(account);
    this.cartItems = CartItemDto.buildFromEntitiesV2(cartItems);
    this.isCartActive = cart.isActive();
    this.totalPrice = getTotalCartPrice(cartItems);
  }

  public long getTotalCartPrice(List<CartItem> cartItems){
    List<CartItemDto> dtos = CartItemDto.buildFromEntitiesV2(cartItems);
    long totalPrice = 0;

    for(CartItemDto item: dtos){
      totalPrice += item.getPrice();
    }

    return totalPrice;
  }

  public static CartDto buildFromEntity(Cart cart){
    return new CartDto(cart.getId(), DateTimeFormatter.get_MMDDYYY_Format(cart.getDateCreated()), cart.getAccount(), cart.isActive());
  }
  public String getCartId() {
    return cartId;
  }

  public void setCartId(String cartId) {
    this.cartId = cartId;
  }

  public String getDateCreated() {
    return dateCreated;
  }

  public void setDateCreated(String dateCreated) {
    this.dateCreated = dateCreated;
  }

  public AccountDto getCustomer() {
    return customer;
  }

  public void setCustomer(Account customer) {
    this.customer = AccountDto.buildFromEntity(customer);
  }

  public boolean isCartActive() {
    return isCartActive;
  }

  public void setCartActive(boolean active) {
    isCartActive = active;
  }

  public List<CartItemDto> getCartItems() {
    return cartItems;
  }

  public void setCartItems(List<CartItemDto> cartItems) {
    this.cartItems = cartItems;
  }

  public long getTotalPrice() {
    return totalPrice;
  }

  public void setTotalPrice(long totalPrice) {
    this.totalPrice = totalPrice;
  }
}
