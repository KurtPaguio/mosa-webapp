package com.example.mosawebapp.cart.domain;

import com.example.mosawebapp.product.threadtype.domain.ThreadType;
import com.example.mosawebapp.product.threadtypedetails.domain.ThreadTypeDetails;
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

  @Query("SELECT item FROM CartItem item WHERE item.cart = :cart")
  CartItem findItemByCart(@Param("cart") Cart cart);

  @Query("SELECT CASE WHEN COUNT(item) > 0 THEN true ELSE false END FROM CartItem item WHERE item.cart = :cart AND item.id = :itemId")
  boolean isCartItemInUserCart(@Param("cart") Cart cart, @Param("itemId") String itemId);

  @Query("SELECT item FROM CartItem item WHERE item.cart = :cart AND item.type = :type AND item.details = :details")
  CartItem isCartFormAlreadyInUserCart(@Param("cart") Cart cart, @Param("type") ThreadType type, @Param("details")
      ThreadTypeDetails details);
}
