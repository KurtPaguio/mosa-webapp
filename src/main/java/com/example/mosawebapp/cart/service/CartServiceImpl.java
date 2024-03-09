package com.example.mosawebapp.cart.service;

import com.example.mosawebapp.account.domain.Account;
import com.example.mosawebapp.account.domain.AccountRepository;
import com.example.mosawebapp.cart.domain.Cart;
import com.example.mosawebapp.cart.domain.CartItem;
import com.example.mosawebapp.cart.domain.CartItemRepository;
import com.example.mosawebapp.cart.domain.CartRepository;
import com.example.mosawebapp.cart.dto.CartDto;
import com.example.mosawebapp.cart.dto.CartItemDto;
import com.example.mosawebapp.cart.dto.CartItemForm;
import com.example.mosawebapp.exceptions.NotFoundException;
import com.example.mosawebapp.exceptions.ValidationException;
import com.example.mosawebapp.security.JwtGenerator;
import com.example.mosawebapp.validate.Validate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartServiceImpl implements CartService{
  private static String PRODUCT_NOT_EXIST = "Product does not exists";
  private final CartRepository cartRepository;
  private final AccountRepository accountRepository;
  private final CartItemRepository cartItemRepository;
  private final JwtGenerator jwtGenerator;

  @Autowired
  public CartServiceImpl(CartRepository cartRepository,
      AccountRepository accountRepository, CartItemRepository cartItemRepository, JwtGenerator jwtGenerator) {
    this.cartRepository = cartRepository;
    this.accountRepository = accountRepository;
    this.cartItemRepository = cartItemRepository;
    this.jwtGenerator = jwtGenerator;
  }

  @Override
  public CartDto getCart(String token) {
    Account account = getAccountFromToken(token);

    Cart cart = cartRepository.findByAccountAndIsActiveLatest(account, true);

    if(cart == null){
      return null;
    }

    List<CartItem> cartItems = cartItemRepository.findByCart(cart);

    return new CartDto(cart, account, cartItems);
  }

  @Override
  public CartItem addCartItem(String token, CartItemForm form) {
    Validate.notNull(form);

    return null;
  }

  @Override
  public void removeCartItem(String token, String itemId) {
    Account account = getAccountFromToken(token);
    CartItem cartItem = cartItemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Cart Item does not exists"));
    Cart cart = cartRepository.findByAccount(account);

    boolean isCartItemInUserCart = false;

    if(!isCartItemInUserCart){
      throw new ValidationException("Cart Item does not belong in user's cart");
    }

    cartItemRepository.delete(cartItem);
  }

  @Override
  public CartItem subtractCartItemQuantity(String token, String itemId) {
    return null;
  }

  @Override
  public CartItem addCartItemQuantity(String token, String itemId) {
    return null;
  }

  private Account getAccountFromToken(String token){
    String id = jwtGenerator.getUserFromJWT(token);

    return accountRepository.findById(id).orElseThrow(() -> new NotFoundException("Account does not exists"));
  }
}
