package com.example.mosawebapp.cart.domain;

import com.example.mosawebapp.product.threadtype.domain.ThreadType;
import java.util.Date;
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
public class CartItem {
  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private String id;
  @CreationTimestamp
  private Date dateCreated;
  @ManyToOne
  @JoinColumn(name = "cart_id")
  private Cart cart;
  @ManyToOne
  @JoinColumn(name = "thread_type_id")
  private ThreadType type;
  @Column
  private Long quantity;
  public CartItem(){}

  public CartItem(Cart cart, ThreadType type, Long quantity) {
    this.cart = cart;
    this.type = type;
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

  public Cart getCart() {
    return cart;
  }

  public void setCart(Cart cart) {
    this.cart = cart;
  }

  public ThreadType getType() {
    return type;
  }

  public void setType(ThreadType type) {
    this.type = type;
  }
  public Long getQuantity() {
    return quantity;
  }

  public void setQuantity(Long quantity) {
    this.quantity = quantity;
  }
}
