package com.example.mosawebapp.product.service;

import com.example.mosawebapp.product.domain.Product;
import com.example.mosawebapp.product.dto.ProductForm;
import java.util.List;

public interface ProductService {
  List<Product> getAllProducts();
  Product getProduct(String id);
  Product addProduct(ProductForm form);
  Product updateProduct(ProductForm form);
}
