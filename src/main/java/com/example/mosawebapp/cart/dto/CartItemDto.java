package com.example.mosawebapp.cart.dto;

import com.example.mosawebapp.cart.domain.Cart;
import com.example.mosawebapp.cart.domain.CartItem;
import com.example.mosawebapp.product.brand.domain.Brand;
import com.example.mosawebapp.product.threadtype.domain.ThreadType;
import com.example.mosawebapp.product.threadtype.dto.ThreadTypeDto;
import com.example.mosawebapp.utils.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CartItemDto {
  private String itemId;
  private String dateAdded;
  private String brandName;
  private ThreadTypeDto threadType;
  private long quantity;
  private long price;

  public CartItemDto(){}

  public CartItemDto(String itemId, String dateAdded, long quantity, long price) {
    this.itemId = itemId;
    this.dateAdded = dateAdded;
    this.quantity = quantity;
    this.price = price;
  }

  public static CartItemDto buildFromEntity(CartItem item){
    return new CartItemDto(item.getId(), DateTimeFormatter.get_MMDDYYY_Format(item.getDateCreated()), item.getQuantity(), 0);
  }

  public static List<CartItemDto> buildFromEntities(List<CartItem> items){
    List<CartItemDto> dto = new ArrayList<>();

    for(CartItem item: items){
      dto.add(buildFromEntity(item));
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

  public ThreadTypeDto getThreadType() {
    return threadType;
  }

  public void setThreadType(ThreadTypeDto threadType) {
    this.threadType = threadType;
  }

  public long getQuantity() {
    return quantity;
  }

  public void setQuantity(long quantity) {
    this.quantity = quantity;
  }

  public long getPrice() {
    return price;
  }

  public void setPrice(long price) {
    this.price = price;
  }
}
