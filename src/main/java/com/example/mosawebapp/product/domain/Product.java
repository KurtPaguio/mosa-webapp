package com.example.mosawebapp.product.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.hibernate.annotations.GenericGenerator;

@Entity
public class Product {
  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private String id;
  @Column
  private String dateCreated;
  @Column
  private String name;
  @Column
  private long grossPrice;
  @Column
  private String size;
  @Column
  private String plyRating;
  @Column
  private String threadType;
  @Column
  private long stocks;

  public Product(){}
  public Product(String dateCreated, String name, long grossPrice, String size, String plyRating,
      String threadType, long stocks) {
    this.dateCreated = dateCreated;
    this.name = name;
    this.grossPrice = grossPrice;
    this.size = size;
    this.plyRating = plyRating;
    this.threadType = threadType;
    this.stocks = stocks;
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
