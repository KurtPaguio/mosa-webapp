package com.example.mosawebapp.kiosk.domain;

import com.example.mosawebapp.kiosk.QueueingNumberService;
import java.util.Date;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Transient;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
public class Kiosk {
  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private String id;
  @CreationTimestamp
  private Date dateCreated;
  @Column
  private String kioskNumber;
  @Column
  private boolean isActive;

  @PrePersist
  public void generateKioskNumber(){
    this.kioskNumber = QueueingNumberService.getNextQueueingNumber();
  }

  public Kiosk(){}

  public Kiosk(boolean isActive) {
    this.isActive = isActive;
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

  public String getKioskNumber() {
    return kioskNumber;
  }

  public void setKioskNumber(String kioskNumber) {
    this.kioskNumber = kioskNumber;
  }

  public boolean isActive() {
    return isActive;
  }

  public void setActive(boolean active) {
    isActive = active;
  }
}
