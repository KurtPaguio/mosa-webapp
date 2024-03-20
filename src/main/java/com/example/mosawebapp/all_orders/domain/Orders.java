package com.example.mosawebapp.all_orders.domain;

import java.util.Date;
import java.util.List;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import javax.persistence.JoinColumn;
import org.apache.poi.xwpf.usermodel.Borders;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

@Entity
public class Orders {
  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private String id;
  @CreationTimestamp
  private Date dateCreated;
  @Column
  private String orderIds;
  @Column
  @Enumerated(EnumType.STRING)
  private OrderType orderType;
  @Column
  @Enumerated(EnumType.STRING)
  private OrderStatus orderStatus;
  @Column
  private String referenceNumber;

  public Orders(){}
  public Orders(String orderIds, OrderType orderType, OrderStatus status, String referenceNumber) {
    this.orderIds = orderIds;
    this.orderType = orderType;
    this.orderStatus = status;
    this.referenceNumber = referenceNumber;
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

  public String getOrderIds() {
    return orderIds;
  }

  public void setOrderIds(String orderIds) {
    this.orderIds = orderIds;
  }

  public OrderType getOrderType() {
    return orderType;
  }

  public void setOrderType(OrderType orderType) {
    this.orderType = orderType;
  }

  public OrderStatus getOrderStatus() {
    return orderStatus;
  }

  public void setOrderStatus(OrderStatus orderStatus) {
    this.orderStatus = orderStatus;
  }

  public String getReferenceNumber() {
    return referenceNumber;
  }

  public void setReferenceNumber(String referenceNumber) {
    this.referenceNumber = referenceNumber;
  }
}
