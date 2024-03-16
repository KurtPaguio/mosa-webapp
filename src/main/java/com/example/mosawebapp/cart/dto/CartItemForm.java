package com.example.mosawebapp.cart.dto;

public class CartItemForm {
  private String brand;
  private String threadType;
  private String width;
  private String aspectRatio;
  private String diameter;
  private String sidewall;
  private String plyRating;
  private Long quantity;

  public CartItemForm(){}

  public CartItemForm(String brand, String threadType, String width,
      String aspectRatio, String diameter, String sidewall, String plyRating, Long quantity) {
    this.brand = brand;
    this.threadType = threadType;
    this.width = width;
    this.aspectRatio = aspectRatio;
    this.diameter = diameter;
    this.sidewall = sidewall;
    this.plyRating = plyRating;
    this.quantity = quantity;
  }

  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  public String getThreadType() {
    return threadType;
  }

  public void setThreadType(String threadType) {
    this.threadType = threadType;
  }

  public String getWidth() {
    return width;
  }

  public void setWidth(String width) {
    this.width = width;
  }

  public String getAspectRatio() {
    return aspectRatio;
  }

  public void setAspectRatio(String aspectRatio) {
    this.aspectRatio = aspectRatio;
  }

  public String getDiameter() {
    return diameter;
  }

  public void setDiameter(String diameter) {
    this.diameter = diameter;
  }

  public String getSidewall() {
    return sidewall;
  }

  public void setSidewall(String sidewall) {
    this.sidewall = sidewall;
  }

  public String getPlyRating() {
    return plyRating;
  }

  public void setPlyRating(String plyRating) {
    this.plyRating = plyRating;
  }

  public Long getQuantity() {
    return quantity;
  }

  public void setQuantity(Long quantity) {
    this.quantity = quantity;
  }
}
