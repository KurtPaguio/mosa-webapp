package com.example.mosawebapp.product.service;

import com.example.mosawebapp.product.domain.Product;
import com.example.mosawebapp.product.dto.ProductForm;
import java.util.List;

public interface ProductService {
  List<Product> getAllProducts(String token);
  Product getProduct(String token, String id);
  Product addProduct(String token, ProductForm form);
  Product updateProduct(String id, String token, ProductForm form);
  void deleteProduct(String id, String token);
}
