package com.example.mosawebapp.onsite_order.domain;

import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

@Entity
public class OnsiteOrderCheckout {
  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private String id;
  @CreationTimestamp
  private Date dateCreated;
  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "order_id")
  private OnsiteOrder order;

  public OnsiteOrderCheckout(){}
  public OnsiteOrderCheckout(OnsiteOrder order) {
    this.order = order;
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

  public OnsiteOrder getOrder() {
    return order;
  }

  public void setOrder(OnsiteOrder order) {
    this.order = order;
  }
}
