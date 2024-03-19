package com.example.mosawebapp.cart.controller;

import com.example.mosawebapp.api_response.ApiResponse;
import com.example.mosawebapp.cart.dto.CartDto;
import com.example.mosawebapp.cart.dto.CartForm;
import com.example.mosawebapp.cart.dto.CheckoutForm;
import com.example.mosawebapp.cart.service.CartService;
import com.example.mosawebapp.exceptions.TokenException;
import com.example.mosawebapp.security.JwtGenerator;
import com.example.mosawebapp.security.domain.TokenBlacklistingService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
public class CartController {
  private static final String BEARER = "Bearer ";
  private static final String TOKEN_INVALID = "Token Invalid/Expired";
  private final TokenBlacklistingService tokenBlacklistingService;
  private final JwtGenerator jwtGenerator;
  private final CartService cartService;

  public CartController(TokenBlacklistingService tokenBlacklistingService,
      JwtGenerator jwtGenerator, CartService cartService) {
    this.tokenBlacklistingService = tokenBlacklistingService;
    this.jwtGenerator = jwtGenerator;
    this.cartService = cartService;
  }

  @GetMapping(value = "/getAllOrders")
  public ResponseEntity<?> getAllOrders(@RequestHeader("Authorization") String header){
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    return ResponseEntity.ok(cartService.getAllCartOrders(token));
  }

  @GetMapping(value = "/getCurrentUserOrders")
  public ResponseEntity<?> getCurrentUserOrders(@RequestHeader("Authorization") String header){
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    List<CartDto> dto = cartService.getAllCurrentUserOrders(token);

    if(dto == null || dto.isEmpty()){
      return ResponseEntity.ok(new ApiResponse("User has no orders yet", HttpStatus.OK));
    }

    return ResponseEntity.ok(dto);
  }

  @GetMapping(value = "/getUserCurrentOrders")
  public ResponseEntity<?> getUserCurrentOrders(@RequestHeader("Authorization") String header){
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    List<CartDto> dto = cartService.getAllUserCurrentOrders(token);

    if(dto == null || dto.isEmpty()){
      return ResponseEntity.ok(new ApiResponse("User has no current orders yet", HttpStatus.OK));
    }

    return ResponseEntity.ok(dto);
  }

  @GetMapping(value = "/getOrder/{id}")
  public ResponseEntity<?> getOrder(@PathVariable("id") String id, @RequestHeader("Authorization") String header){
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    return ResponseEntity.ok(cartService.getCartOrder(token, id));
  }

  @PostMapping(value = "/addOrder")
  public ResponseEntity<?> addOrder(@RequestHeader("Authorization") String header, @RequestBody
      CartForm form){
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    return ResponseEntity.ok(cartService.addCartOrder(token, form));
  }

  @GetMapping(value = "/addOrderQuantity/{id}")
  public ResponseEntity<?> addOrderQuantity(@PathVariable("id") String id, @RequestHeader("Authorization") String header){
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    return ResponseEntity.ok(cartService.addCartOrderQuantity(token, id));
  }

  @DeleteMapping(value = "/removeOrder/{id}")
  public ResponseEntity<?> removeOrder(@PathVariable("id") String id, @RequestHeader("Authorization") String header){
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);
    cartService.removeCartOrder(token, id);

    return ResponseEntity.ok(new ApiResponse("Cart order deleted", HttpStatus.OK));
  }

  @GetMapping(value = "/subtractOrderQuantity/{id}")
  public ResponseEntity<?> subtractOrderQuantity(@PathVariable("id") String id, @RequestHeader("Authorization") String header){
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    return ResponseEntity.ok(cartService.subtractCartOrderQuantity(token, id));
  }

  @PostMapping(value = "/checkout")
  public ResponseEntity<?> checkout(@RequestHeader("Authorization") String header, @RequestBody
      CheckoutForm form){
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);
    cartService.checkout(token, form);

    return ResponseEntity.ok(new ApiResponse("Checkout successfully", HttpStatus.OK));
  }

  private void validateTokenValidity(String token){
    if(!jwtGenerator.isTokenValid(token) || token.isEmpty() || tokenBlacklistingService.isTokenBlacklisted(token)){
      throw new TokenException(TOKEN_INVALID);
    }
  }
}
