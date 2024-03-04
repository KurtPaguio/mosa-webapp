package com.example.mosawebapp.product.domain;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

@Entity
public class Product {
  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private String id;
  @CreationTimestamp
  private Date dateCreated;
  @Column
  private String name;
  @Column
  private Long grossPrice;
  @Column
  private String size;
  @Column
  private String plyRating;
  @Column
  private String threadType;
  @Column
  private Long stocks;

  public Product(){}
  public Product(String name, Long grossPrice, String size, String plyRating,
      String threadType, Long stocks) {
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

  public Date getDateCreated() {
    return dateCreated;
  }

  public void setDateCreated(Date dateCreated) {
    this.dateCreated = dateCreated;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Long getGrossPrice() {
    return grossPrice;
  }

  public void setGrossPrice(Long grossPrice) {
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

  public Long getStocks() {
    return stocks;
  }

  public void setStocks(Long stocks) {
    this.stocks = stocks;
  }
}
