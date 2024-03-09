package com.example.mosawebapp.cart.dto;

public class CartItemForm {
  private String productId;
  private long quantity;

  public CartItemForm(){}

  public CartItemForm(String productId, long quantity) {
    this.productId = productId;
    this.quantity = quantity;
  }

  public String getProductId() {
    return productId;
  }

  public void setProductId(String productId) {
    this.productId = productId;
  }

  public long getQuantity() {
    return quantity;
  }

  public void setQuantity(long quantity) {
    this.quantity = quantity;
  }
}
