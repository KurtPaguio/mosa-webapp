package com.example.mosawebapp.kiosk.domain;

import com.example.mosawebapp.all_orders.domain.OrderStatus;
import com.example.mosawebapp.all_orders.domain.Orders;
import com.example.mosawebapp.onsite_order.domain.OnsiteOrder;
import com.example.mosawebapp.product.threadtype.domain.ThreadType;
import com.example.mosawebapp.product.threadtypedetails.domain.ThreadTypeDetails;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

@Entity
public class Kiosk {
  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private String id;
  @CreationTimestamp
  private Date dateCreated;
  @Column
  private String token;
  @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinColumn(name = "thread_type_id")
  private ThreadType type;
  @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinColumn(name = "details_id")
  private ThreadTypeDetails details;
  @Column
  private long quantity;
  @Column
  private float totalPrice;
  @Column
  private boolean isCheckedOut;
  @Column
  private Long queueingNumber;

  @OneToMany(mappedBy = "kiosk", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Orders> kiosks = new ArrayList<>();
  public Kiosk(){}
  public Kiosk(String token, ThreadType type, ThreadTypeDetails details, long quantity, float totalPrice,
      boolean isCheckedOut) {
    this.token = token;
    this.type = type;
    this.details = details;
    this.quantity = quantity;
    this.totalPrice = totalPrice;
    this.isCheckedOut = isCheckedOut;
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

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
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

  public long getQuantity() {
    return quantity;
  }

  public void setQuantity(long quantity) {
    this.quantity = quantity;
  }

  public float getTotalPrice() {
    return totalPrice;
  }

  public void setTotalPrice(float totalPrice) {
    this.totalPrice = totalPrice;
  }

  public boolean isCheckedOut() {
    return isCheckedOut;
  }

  public void setCheckedOut(boolean checkedOut) {
    isCheckedOut = checkedOut;
  }

  public Long getQueueingNumber() {
    return queueingNumber;
  }

  public void setQueueingNumber(Long queueingNumber) {
    this.queueingNumber = queueingNumber;
  }

  public List<Orders> getKiosks() {
    return kiosks;
  }

  public void setKiosks(List<Orders> kiosks) {
    this.kiosks = kiosks;
  }
}
