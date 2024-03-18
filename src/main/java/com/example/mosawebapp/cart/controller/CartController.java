package com.example.mosawebapp.cart.controller;

import com.example.mosawebapp.api_response.ApiObjectResponse;
import com.example.mosawebapp.api_response.ApiResponse;
import com.example.mosawebapp.cart.dto.CartDto;
import com.example.mosawebapp.cart.dto.CartItemForm;
import com.example.mosawebapp.cart.service.CartService;
import com.example.mosawebapp.exceptions.TokenException;
import com.example.mosawebapp.security.JwtGenerator;
import com.example.mosawebapp.security.domain.TokenBlacklistingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:8080"})
public class CartController {
  private static final Logger logger = LoggerFactory.getLogger(CartController.class);
  private static final String BEARER = "Bearer ";
  private static final String TOKEN_INVALID = "Token Invalid/Expired";
  private final CartService cartService;
  private final JwtGenerator jwtGenerator;
  private final TokenBlacklistingService tokenBlacklistingService;

  @Autowired
  public CartController(CartService cartService, JwtGenerator jwtGenerator,
      TokenBlacklistingService tokenBlacklistingService) {
    this.cartService = cartService;
    this.jwtGenerator = jwtGenerator;
    this.tokenBlacklistingService = tokenBlacklistingService;
  }

  @GetMapping(value = "/getCheckouts")
  public ResponseEntity<?> getCheckouts(@RequestHeader("Authorization") String header){
    logger.info("getting cart checkouts");
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    return ResponseEntity.ok(cartService.getCheckouts(token));
  }

  @GetMapping(value = "/getCart")
  public ResponseEntity<?> getCart(@RequestHeader("Authorization") String header){
    logger.info("getting cart");
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    CartDto dto = cartService.getCart(token);

    if(dto == null){
      return ResponseEntity.ok(new ApiResponse("No cart yet", HttpStatus.OK));
    }

    return ResponseEntity.ok(dto);
  }

  @GetMapping(value = "/subtractItemQuantity/{itemId}")
  public ResponseEntity<?> subtractItemQuantityInCart(@RequestHeader("Authorization") String header, @PathVariable("itemId") String itemId){
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    return cartService.subtractCartItemQuantity(token, itemId);
  }

  @GetMapping(value = "/addItemQuantity/{itemId}")
  public ResponseEntity<?> addItemQuantityInCart(@RequestHeader("Authorization") String header, @PathVariable("itemId") String itemId){
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    return cartService.addCartItemQuantity(token, itemId);
  }

  @GetMapping(value = "/checkout")
  public ResponseEntity<?> checkoutCart(@RequestHeader("Authorization") String header){
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);
    cartService.checkout(token);

    return ResponseEntity.ok(new ApiResponse("Cart checkout successfully", HttpStatus.OK));
  }

  @PostMapping(value = "/addItem")
  public ResponseEntity<?> addItemToCart(@RequestHeader("Authorization") String header, @RequestBody
      CartItemForm form){
    logger.info("adding item {}", form);
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    return ResponseEntity.ok(new ApiObjectResponse(HttpStatus.CREATED, "Item added to cart", cartService.addCartItem(token, form)));
  }

  @DeleteMapping(value = "/removeItem/{itemId}")
  public ResponseEntity<?> removeItemInCart(@PathVariable("itemId") String id, @RequestHeader("Authorization") String header){
    logger.info("removing item {}", id);
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    cartService.removeCartItem(token, id);

    logger.info("Cart Item removed from the cart");
    return ResponseEntity.ok(new ApiResponse("Cart item removed from the cart", HttpStatus.OK));
  }

  private void validateTokenValidity(String token){
    if(!jwtGenerator.isTokenValid(token) || token.isEmpty() || tokenBlacklistingService.isTokenBlacklisted(token)){
      throw new TokenException(TOKEN_INVALID);
    }
  }
}
