package com.example.mosawebapp.product.threadtype.domain;

import com.example.mosawebapp.cart.domain.Cart;
import com.example.mosawebapp.kiosk.domain.Kiosk;
import com.example.mosawebapp.onsite_order.domain.OnsiteOrder;
import com.example.mosawebapp.product.brand.domain.Brand;
import com.example.mosawebapp.product.threadtypedetails.domain.ThreadTypeDetails;
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
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

@Entity
public class ThreadType {
  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private String id;
  @CreationTimestamp
  private Date dateCreated;
  @Column
  private String type;
  @Column
  private int rating;
  @Column
  private String imageUrl;
  @Column
  private String description;
  @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinColumn(name = "brand_id")
  private Brand brand;
  @OneToMany(mappedBy = "threadType", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ThreadTypeDetails> details = new ArrayList<>();

  public ThreadType(){}

  public ThreadType(String type, String imageUrl,
      String description, Brand brand) {
    this.type = type;
    this.imageUrl = imageUrl;
    this.description = description;
    this.brand = brand;
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

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public int getRating() {
    return rating;
  }

  public void setRating(int rating) {
    this.rating = rating;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Brand getBrand() {
    return brand;
  }

  public void setBrand(Brand brand) {
    this.brand = brand;
  }

  public List<ThreadTypeDetails> getDetails() {
    return details;
  }

  public void setDetails(
      List<ThreadTypeDetails> details) {
    this.details = details;
  }


}
