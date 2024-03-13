package com.example.mosawebapp.product.threadtypedetails.domain;

import com.example.mosawebapp.product.threadtype.domain.ThreadType;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

@Entity
public class ThreadTypeDetails {
  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private String id;
  @CreationTimestamp
  private Date dateCreated;
  @Column
  private String width;
  @Column
  private String aspectRatio;
  @Column
  private String diameter;
  @Column
  private String sidewall;
  @Column
  private String plyRating;
  @Column
  private Long price;
  @Column
  private Long stocks;
  @ManyToOne
  @JoinColumn(name = "thread_type_id")
  private ThreadType threadType;

  public ThreadTypeDetails(){}

  public ThreadTypeDetails(String width, String aspectRatio, String diameter, String sidewall,
      String plyRating, Long stocks, Long price, ThreadType threadType) {
    this.width = width;
    this.aspectRatio = aspectRatio;
    this.diameter = diameter;
    this.sidewall = sidewall;
    this.plyRating = plyRating;
    this.price = price;
    this.stocks = stocks;
    this.threadType = threadType;
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

  public Long getPrice() {
    return price;
  }

  public void setPrice(Long price) {
    this.price = price;
  }

  public Long getStocks() {
    return stocks;
  }

  public void setStocks(Long stocks) {
    this.stocks = stocks;
  }

  public ThreadType getThreadType() {
    return threadType;
  }

  public void setThreadType(ThreadType threadType) {
    this.threadType = threadType;
  }
}
