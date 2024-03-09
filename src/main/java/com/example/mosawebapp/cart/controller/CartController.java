package com.example.mosawebapp.cart.controller;

import com.example.mosawebapp.account.dto.AccountDto;
import com.example.mosawebapp.apiresponse.ApiErrorResponse;
import com.example.mosawebapp.apiresponse.ApiObjectResponse;
import com.example.mosawebapp.apiresponse.ApiResponse;
import com.example.mosawebapp.cart.dto.CartDto;
import com.example.mosawebapp.cart.dto.CartItemDto;
import com.example.mosawebapp.cart.dto.CartItemForm;
import com.example.mosawebapp.cart.service.CartService;
import com.example.mosawebapp.exceptions.NotFoundException;
import com.example.mosawebapp.exceptions.SecurityException;
import com.example.mosawebapp.exceptions.TokenException;
import com.example.mosawebapp.exceptions.ValidationException;
import com.example.mosawebapp.security.JwtGenerator;
import com.example.mosawebapp.security.domain.TokenBlacklistingService;
import com.example.mosawebapp.utils.DateTimeFormatter;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
public class CartController {
  private static final Logger logger = LoggerFactory.getLogger(CartController.class);
  private static final String BEARER = "Bearer ";
  private static final String TOKEN_INVALID = "Token Invalid/Expired";
  private final CartService cartService;
  private final JwtGenerator jwtGenerator;
  private final TokenBlacklistingService tokenBlacklistingService;
  public CartController(CartService cartService, JwtGenerator jwtGenerator,
      TokenBlacklistingService tokenBlacklistingService) {
    this.cartService = cartService;
    this.jwtGenerator = jwtGenerator;
    this.tokenBlacklistingService = tokenBlacklistingService;
  }

  @GetMapping(value = "/getCart")
  public ResponseEntity<?> getCart(@RequestHeader("Authorization") String header){
    logger.info("getting cart");
    String token = header.replace(BEARER, "");

    try{
      validateTokenValidity(token);

      CartDto dto = cartService.getCart(token);

      if(dto == null){
        return ResponseEntity.ok(new ApiResponse("No cart yet", HttpStatus.OK));
      }

      return ResponseEntity.ok(dto);
    } catch(SecurityException se){
      return new ResponseEntity<>(new ApiErrorResponse(
          DateTimeFormatter.get_MMDDYYY_Format(new Date()),"500", HttpStatus.INTERNAL_SERVER_ERROR, se.getMessage()),
          HttpStatus.INTERNAL_SERVER_ERROR);
    } catch(NotFoundException | NullPointerException ne){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"404", HttpStatus.NOT_FOUND, ne.getMessage()),
          HttpStatus.BAD_REQUEST);
    } catch(TokenException te){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"401", HttpStatus.UNAUTHORIZED, te.getMessage()),
          HttpStatus.UNAUTHORIZED);
    } catch(ValidationException | NumberFormatException e){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"400", HttpStatus.BAD_REQUEST, e.getMessage()),
          HttpStatus.BAD_REQUEST);
    }
  }
  @PostMapping(value = "/addItem")
  public ResponseEntity<?> addItemToCart(@RequestHeader("Authorization") String header, @RequestBody
      CartItemForm form){
    logger.info("adding item {}", form.getProductId());
    String token = header.replace(BEARER, "");

    try{
      validateTokenValidity(token);

      CartItemDto dto = CartItemDto.buildFromEntity(cartService.addCartItem(token, form));

      //logger.info("item {} - {} added to cart", dto.getProduct().getBrand(), dto.getProduct().getThreadType());
      return ResponseEntity.ok(new ApiObjectResponse(HttpStatus.CREATED, "Item added to cart", dto));
    } catch(SecurityException se){
      return new ResponseEntity<>(new ApiErrorResponse(
          DateTimeFormatter.get_MMDDYYY_Format(new Date()),"500", HttpStatus.INTERNAL_SERVER_ERROR, se.getMessage()),
          HttpStatus.INTERNAL_SERVER_ERROR);
    } catch(NotFoundException | NullPointerException ne){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"404", HttpStatus.NOT_FOUND, ne.getMessage()),
          HttpStatus.BAD_REQUEST);
    } catch(TokenException te){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"401", HttpStatus.UNAUTHORIZED, te.getMessage()),
          HttpStatus.UNAUTHORIZED);
    } catch(ValidationException | NumberFormatException e){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"400", HttpStatus.BAD_REQUEST, e.getMessage()),
          HttpStatus.BAD_REQUEST);
    }
  }

  @DeleteMapping(value = "/removeItem/{itemId}")
  public ResponseEntity<?> removeItemInCart(@PathVariable("itemId") String id, @RequestHeader("Authorization") String header){
    logger.info("removing item {}", id);
    String token = header.replace(BEARER, "");

    try{
      validateTokenValidity(token);

      cartService.removeCartItem(token, id);

      logger.info("Cart Item removed from the cart");
      return ResponseEntity.ok(new ApiResponse("Cart Item removed from the cart", HttpStatus.OK));
    } catch(SecurityException se){
      return new ResponseEntity<>(new ApiErrorResponse(
          DateTimeFormatter.get_MMDDYYY_Format(new Date()),"500", HttpStatus.INTERNAL_SERVER_ERROR, se.getMessage()),
          HttpStatus.INTERNAL_SERVER_ERROR);
    } catch(NotFoundException | NullPointerException ne){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"404", HttpStatus.NOT_FOUND, ne.getMessage()),
          HttpStatus.BAD_REQUEST);
    } catch(TokenException te){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"401", HttpStatus.UNAUTHORIZED, te.getMessage()),
          HttpStatus.UNAUTHORIZED);
    } catch(ValidationException | NumberFormatException e){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"400", HttpStatus.BAD_REQUEST, e.getMessage()),
          HttpStatus.BAD_REQUEST);
    }
  }

  private void validateTokenValidity(String token){
    if(!jwtGenerator.isTokenValid(token) || token.isEmpty() || tokenBlacklistingService.isTokenBlacklisted(token)){
      throw new TokenException(TOKEN_INVALID);
    }
  }
}
