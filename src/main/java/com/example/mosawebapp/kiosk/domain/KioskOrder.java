package com.example.mosawebapp.kiosk.domain;

import com.example.mosawebapp.cart.domain.Cart;
import com.example.mosawebapp.product.threadtype.domain.ThreadType;
import com.example.mosawebapp.product.threadtypedetails.domain.ThreadTypeDetails;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

@Entity
public class KioskOrder {
  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private String id;
  @CreationTimestamp
  private Date dateCreated;
  @ManyToOne
  @JoinColumn(name = "kiosk_id")
  private Kiosk kiosk;
  @ManyToOne
  @JoinColumn(name = "thread_type_id")
  private ThreadType type;
  @ManyToOne
  @JoinColumn(name = "details_id")
  private ThreadTypeDetails details;
  @Column
  private Long quantity;

  public KioskOrder(){}

  public KioskOrder(Kiosk kiosk, ThreadType type, ThreadTypeDetails details, Long quantity) {
    this.kiosk = kiosk;
    this.type = type;
    this.details = details;
    this.quantity = quantity;
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

  public Kiosk getKiosk() {
    return kiosk;
  }

  public void setKiosk(Kiosk kiosk) {
    this.kiosk = kiosk;
  }

  public ThreadType getType() {
    return type;
  }

  public void setType(ThreadType type) {
    this.type = type;
  }

  public ThreadTypeDetails getDetails() {
    return details;
  }

  public void setDetails(ThreadTypeDetails details) {
    this.details = details;
  }

  public Long getQuantity() {
    return quantity;
  }

  public void setQuantity(Long quantity) {
    this.quantity = quantity;
  }
}
