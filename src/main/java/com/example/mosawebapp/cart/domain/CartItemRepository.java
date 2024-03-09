package com.example.mosawebapp.cart.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, String>,
    JpaSpecificationExecutor {

  List<CartItem> findByCart(Cart cart);

  @Query("SELECT CASE WHEN COUNT(item) > 0 THEN true ELSE false END FROM CartItem item WHERE item.cart = :cart AND item.id = :itemId")
  boolean isCartItemInUserCart(@Param("cart") Cart cart, @Param("itemId") String itemId);
}
