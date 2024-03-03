package com.example.mosawebapp.product.dto;

public class ProductForm {
  private String dateCreated;
  private String name;
  private long grossPrice;
  private String size;
  private String plyRating;
  private String threadType;
  private long stocks;

  private ProductForm(){}
  public ProductForm(String dateCreated, String name, long grossPrice, String size,
      String plyRating,
      String threadType, long stocks) {
    this.dateCreated = dateCreated;
    this.name = name;
    this.grossPrice = grossPrice;
    this.size = size;
    this.plyRating = plyRating;
    this.threadType = threadType;
    this.stocks = stocks;
  }

  public String getDateCreated() {
    return dateCreated;
  }

  public void setDateCreated(String dateCreated) {
    this.dateCreated = dateCreated;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public long getGrossPrice() {
    return grossPrice;
  }

  public void setGrossPrice(long grossPrice) {
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

  public long getStocks() {
    return stocks;
  }

  public void setStocks(long stocks) {
    this.stocks = stocks;
  }
}
