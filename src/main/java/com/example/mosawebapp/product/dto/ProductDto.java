package com.example.mosawebapp.product.dto;

import com.example.mosawebapp.product.domain.Product;
import java.util.ArrayList;
import java.util.List;

public class ProductDto {
  private String id;
  private String dateCreated;
  private String name;
  private long grossPrice;
  private String size;
  private String plyRating;
  private String threadType;
  private long stocks;

  public ProductDto(){}

  public ProductDto(String id, String dateCreated, String name, long grossPrice, String size,
      String plyRating, String threadType, long stocks) {
    this.id = id;
    this.dateCreated = dateCreated;
    this.name = name;
    this.grossPrice = grossPrice;
    this.size = size;
    this.plyRating = plyRating;
    this.threadType = threadType;
    this.stocks = stocks;
  }

  public static ProductDto buildFromEntity(Product product){
    return new ProductDto(product.getId(), product.getDateCreated(), product.getName(),
        product.getGrossPrice(), product.getSize(), product.getPlyRating(), product.getThreadType(), product.getStocks());
  }

  public static List<ProductDto> buildFromEntities(List<Product> products){
    List<ProductDto> dtos = new ArrayList<>();

    for(Product product: products){
      dtos.add(buildFromEntity(product));
    }

    return dtos;
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
