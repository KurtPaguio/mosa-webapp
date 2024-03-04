package com.example.mosawebapp.product.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, String>,
    JpaSpecificationExecutor<Product> {

  Product findByNameAndThreadType(String name, String threadType);
}
