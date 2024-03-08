package com.example.mosawebapp.cart.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CartRepository extends JpaRepository<Cart, String>, JpaSpecificationExecutor {
}
