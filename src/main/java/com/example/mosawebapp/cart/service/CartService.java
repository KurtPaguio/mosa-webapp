package com.example.mosawebapp.cart.service;

import com.example.mosawebapp.cart.domain.Cart;
import com.example.mosawebapp.cart.domain.CartItem;
import com.example.mosawebapp.cart.dto.CartDto;
import com.example.mosawebapp.cart.dto.CartItemForm;

public interface CartService {
  CartDto getCart(String token);
  CartItem addCartItem(String token, CartItemForm form);
  void removeCartItem(String token, String itemId);
  CartItem subtractCartItemQuantity(String token, String itemId);
  CartItem addCartItemQuantity(String token, String itemId);
}
