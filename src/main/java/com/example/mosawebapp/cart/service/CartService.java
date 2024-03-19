package com.example.mosawebapp.cart.service;

import com.example.mosawebapp.cart.dto.CartDto;
import com.example.mosawebapp.cart.dto.CartForm;
import com.example.mosawebapp.cart.dto.CheckoutForm;
import java.util.List;

public interface CartService {
  List<CartDto> getAllCartOrders(String token);
  List<CartDto> getAllCurrentUserOrders(String token);

  List<CartDto> getAllUserCurrentOrders(String token);

  CartDto getCartOrder(String token, String cartId);
  CartDto addCartOrder(String token, CartForm form);
  CartDto addCartOrderQuantity(String token, String cartId);
  void removeCartOrder(String token, String cartId);
  CartDto subtractCartOrderQuantity(String token, String cartId);
  void checkout(String token, CheckoutForm form);
}
