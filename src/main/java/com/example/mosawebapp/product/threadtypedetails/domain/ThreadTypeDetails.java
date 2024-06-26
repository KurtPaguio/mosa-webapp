package com.example.mosawebapp.product.threadtypedetails.domain;

import com.example.mosawebapp.cart.domain.Cart;
import com.example.mosawebapp.kiosk.domain.Kiosk;
import com.example.mosawebapp.onsite_order.domain.OnsiteOrder;
import com.example.mosawebapp.product.threadtype.domain.ThreadType;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
  private float price;
  @Column
  private Long stocks;
  @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinColumn(name = "thread_type_id")
  private ThreadType threadType;
  @OneToMany(mappedBy = "details", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<OnsiteOrder> orders = new ArrayList<>();
  @OneToMany(mappedBy = "details", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Kiosk> kiosks = new ArrayList<>();
  @OneToMany(mappedBy = "details", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Cart> carts = new ArrayList<>();

  public ThreadTypeDetails(){}

  public ThreadTypeDetails(String width, String aspectRatio, String diameter, String sidewall,
      String plyRating, Long stocks, float price, ThreadType threadType) {
    this.width = width;
    this.aspectRatio = aspectRatio;
    this.diameter = diameter;
    this.sidewall = sidewall;
    this.plyRating = plyRating;
    this.price = price;
    this.stocks = stocks;
    this.threadType = threadType;
  }

  public float roundToNearestHundredths(float price){
    price = Math.round(price * 100) / 100.0f;

    return price;
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

  public float getPrice() {
    return price;
  }

  public void setPrice(float price) {
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

  public long getNumberInRatio(){
    return Long.parseLong(aspectRatio.replaceAll("[^\\d]", ""));
  }

  public List<OnsiteOrder> getOrders() {
    return orders;
  }

  public void setOrders(List<OnsiteOrder> orders) {
    this.orders = orders;
  }

  public List<Kiosk> getKiosks() {
    return kiosks;
  }

  public void setKiosks(List<Kiosk> kiosks) {
    this.kiosks = kiosks;
  }

  public List<Cart> getCarts() {
    return carts;
  }

  public void setCarts(List<Cart> carts) {
    this.carts = carts;
  }
}
