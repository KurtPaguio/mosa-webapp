package com.example.mosawebapp.product.dto;

import org.springframework.lang.Nullable;

public class ProductForm {
  private String name;
  @Nullable
  private Long grossPrice;
  private String size;
  private String plyRating;
  private String threadType;
  @Nullable
  private Long stocks;

  private ProductForm(){}
  public ProductForm(String name, @Nullable Long grossPrice, String size,
      String plyRating, String threadType, @Nullable Long stocks) {
    this.name = name;
    this.grossPrice = grossPrice == null ? 0 : grossPrice;
    this.size = size;
    this.plyRating = plyRating;
    this.threadType = threadType;
    this.stocks = stocks == null ? 0 : stocks;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Nullable
  public Long getGrossPrice() {
    return grossPrice;
  }

  public void setGrossPrice(@Nullable Long grossPrice) {
    this.grossPrice = grossPrice;
  }

  public String getSize() {
    return size;
  }

  public void setSize(String size) {
    this.size = size;
  }

  public String getPlyRating() {
    return plyRating;
  }

  public void setPlyRating(String plyRating) {
    this.plyRating = plyRating;
  }

  public String getThreadType() {
    return threadType;
  }

  public void setThreadType(String threadType) {
    this.threadType = threadType;
  }

  @Nullable
  public Long getStocks() {
    return stocks;
  }

  public void setStocks(@Nullable Long stocks) {
    this.stocks = stocks;
  }
}
