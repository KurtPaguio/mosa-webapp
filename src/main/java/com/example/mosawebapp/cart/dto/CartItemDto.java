package com.example.mosawebapp.cart.dto;

import com.example.mosawebapp.cart.domain.Cart;
import com.example.mosawebapp.cart.domain.CartItem;
import com.example.mosawebapp.product.brand.domain.Brand;
import com.example.mosawebapp.product.threadtype.domain.ThreadType;
import com.example.mosawebapp.product.threadtype.dto.ThreadTypeDto;
import com.example.mosawebapp.product.threadtypedetails.domain.ThreadTypeDetails;
import com.example.mosawebapp.product.threadtypedetails.dto.ThreadTypeDetailsDto;
import com.example.mosawebapp.utils.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CartItemDto {
  private String itemId;
  private String dateAdded;
  private String brandName;
  private String threadType;
  private ThreadTypeDetailsDto details;
  private long quantity;
  private long price;

  public CartItemDto(){}

  public CartItemDto(String itemId, String dateAdded, long quantity, long price) {
    this.itemId = itemId;
    this.dateAdded = dateAdded;
    this.quantity = quantity;
    this.price = price;
  }

  public CartItemDto(CartItem item, ThreadTypeDetails details){
    this.itemId = item.getId();
    this.dateAdded = DateTimeFormatter.get_MMDDYYY_Format(item.getDateCreated());
    this.brandName = item.getType().getBrand().getName();
    this.threadType = item.getType().getType();
    this.details = new ThreadTypeDetailsDto(item.getType(), details);
    this.quantity = item.getQuantity();
    this.price = item.getQuantity() * details.getPrice();
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

  public static List<CartItemDto> buildFromEntitiesV2(List<CartItem> items){
    List<CartItemDto> dto = new ArrayList<>();

    for(CartItem item: items){
      dto.add(new CartItemDto(item, item.getDetails()));
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

  public long getPrice() {
    return price;
  }

  public void setPrice(long price) {
    this.price = price;
  }
}
