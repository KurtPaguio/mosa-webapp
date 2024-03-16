package com.example.mosawebapp.cart.service;

import com.example.mosawebapp.cart.domain.Cart;
import com.example.mosawebapp.cart.domain.CartItem;
import com.example.mosawebapp.cart.dto.CartCheckoutDto;
import com.example.mosawebapp.cart.dto.CartDto;
import com.example.mosawebapp.cart.dto.CartItemDto;
import com.example.mosawebapp.cart.dto.CartItemForm;
import java.util.List;
import org.springframework.http.ResponseEntity;

public interface CartService {

  List<CartCheckoutDto> getCheckouts();

  CartDto getCart(String token);
  CartItemDto addCartItem(String token, CartItemForm form);
  void removeCartItem(String token, String itemId);
  ResponseEntity<?> subtractCartItemQuantity(String token, String itemId);
  ResponseEntity<?> addCartItemQuantity(String token, String itemId);
  void checkout(String token);
}
