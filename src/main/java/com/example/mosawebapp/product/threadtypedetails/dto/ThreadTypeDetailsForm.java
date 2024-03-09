package com.example.mosawebapp.product.threadtypedetails.dto;

import org.springframework.lang.Nullable;

public class ThreadTypeDetailsForm {
  private String threadType;
  private String width;
  private String aspectRatio;
  private String diameter;
  private Long price;
  @Nullable
  private Long stocks;

  public ThreadTypeDetailsForm(){}

  public ThreadTypeDetailsForm(String threadType, String width, String aspectRatio, String diameter,
      Long price, @Nullable Long stocks) {
    this.threadType = threadType;
    this.width = width;
    this.aspectRatio = aspectRatio;
    this.diameter = diameter;
    this.price = price;
    this.stocks = stocks;
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

  public Long getPrice() {
    return price;
  }

  public void setPrice(Long price) {
    this.price = price;
  }

  @Nullable
  public Long getStocks() {
    return stocks;
  }

  public void setStocks(@Nullable Long stocks) {
    this.stocks = stocks;
  }
}
