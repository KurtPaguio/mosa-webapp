package com.example.mosawebapp.all_orders.controller;

import com.example.mosawebapp.all_orders.service.OrdersService;
import com.example.mosawebapp.exceptions.TokenException;
import com.example.mosawebapp.security.JwtGenerator;
import com.example.mosawebapp.security.domain.TokenBlacklistingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrdersController {
  private static final String BEARER = "Bearer ";
  private static final String TOKEN_INVALID = "Token Invalid/Expired";
  private final TokenBlacklistingService tokenBlacklistingService;
  private final JwtGenerator jwtGenerator;
  private final OrdersService ordersService;

  public OrdersController(TokenBlacklistingService tokenBlacklistingService,
      JwtGenerator jwtGenerator, OrdersService ordersService) {
    this.tokenBlacklistingService = tokenBlacklistingService;
    this.jwtGenerator = jwtGenerator;
    this.ordersService = ordersService;
  }

  @GetMapping(value = "/getAllOrders")
  public ResponseEntity<?> getAllOrders(@RequestHeader("Authorization") String header){
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);
    return ResponseEntity.ok(ordersService.getAllOrders(token));
  }

  private void validateTokenValidity(String token){
    if(!jwtGenerator.isTokenValid(token) || token.isEmpty() || tokenBlacklistingService.isTokenBlacklisted(token)){
      throw new TokenException(TOKEN_INVALID);
    }
  }
}
