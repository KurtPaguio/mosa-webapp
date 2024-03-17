package com.example.mosawebapp.kiosk.domain;

import com.example.mosawebapp.account.domain.Account;
import com.example.mosawebapp.cart.domain.Cart;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

@Entity
public class KioskCheckout {
  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private String id;
  @CreationTimestamp
  private Date dateCreated;
  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "kiosk_id")
  private Kiosk kiosk;

  public KioskCheckout(){}
  public KioskCheckout(Kiosk kiosk) {
    this.kiosk = kiosk;
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
}
