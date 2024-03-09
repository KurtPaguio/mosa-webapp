package com.example.mosawebapp.product.brand.dto;

public class BrandForm {
  private String brandName;

  public BrandForm(){}
  public BrandForm(String brandName) {
    this.brandName = brandName;
  }

  public String getBrandName() {
    return brandName;
  }

  public void setBrandName(String brandName) {
    this.brandName = brandName;
  }
}
