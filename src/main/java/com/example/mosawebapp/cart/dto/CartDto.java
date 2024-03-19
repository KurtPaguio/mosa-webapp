package com.example.mosawebapp.cart.dto;

import com.example.mosawebapp.cart.domain.Cart;
import com.example.mosawebapp.product.threadtypedetails.domain.ThreadTypeDetails;
import com.example.mosawebapp.product.threadtypedetails.dto.ThreadTypeDetailsDto;
import com.example.mosawebapp.utils.DateTimeFormatter;

public class CartDto {
  private String id;
  private String dateCreated;
  private String customerName;
  private String brandName;
  private String threadType;
  private ThreadTypeDetailsDto details;
  private long quantity;
  private float totalPrice;

  public CartDto(){}

  public CartDto(String id, String dateCreated, String brandName, String threadType,
      ThreadTypeDetails details, long quantity, float totalPrice) {
    this.id = id;
    this.dateCreated = dateCreated;
    this.brandName = brandName;
    this.threadType = threadType;
    this.details = ThreadTypeDetailsDto.buildFromEntity(details);
    this.quantity = quantity;
    this.totalPrice = totalPrice;
  }

  public CartDto(Cart cart){
    this.id = cart.getId();
    this.dateCreated = DateTimeFormatter.get_MMDDYYY_Format(cart.getDateCreated());
    this.customerName = validateCustomerName(cart);
    this.brandName = cart.getType().getBrand().getName();
    this.threadType = cart.getType().getType();
    this.details = ThreadTypeDetailsDto.buildFromEntity(cart.getDetails());
    this.quantity = cart.getQuantity();
    this.totalPrice = cart.getQuantity() * details.getPrice();
  }

  public String validateCustomerName(Cart cart){
    if(cart.getAccount().getFullName() == null || cart.getAccount().getFullName().isEmpty()){
      return cart.getAccount().getEmail() + " (No Name)";
    }

    return cart.getAccount().getFullName();
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getDateCreated() {
    return dateCreated;
  }

  public void setDateCreated(String dateCreated) {
    this.dateCreated = dateCreated;
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

  public float getTotalPrice() {
    return totalPrice;
  }

  public void setTotalPrice(float totalPrice) {
    this.totalPrice = totalPrice;
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }
}
