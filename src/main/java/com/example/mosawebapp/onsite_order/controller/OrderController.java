package com.example.mosawebapp.onsite_order.controller;

import com.example.mosawebapp.api_response.ApiObjectResponse;
import com.example.mosawebapp.api_response.ApiResponse;
import com.example.mosawebapp.exceptions.TokenException;
import com.example.mosawebapp.kiosk.controller.KioskController;
import com.example.mosawebapp.onsite_order.dto.OnsiteOrderDto;
import com.example.mosawebapp.onsite_order.dto.OrderForm;
import com.example.mosawebapp.onsite_order.service.OnsiteOrderService;
import com.example.mosawebapp.security.JwtGenerator;
import com.example.mosawebapp.security.domain.TokenBlacklistingService;
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
@RequestMapping("/api/onsiteOrder")
public class OrderController {
  private static final Logger logger = LoggerFactory.getLogger(KioskController.class);
  private static final String BEARER = "Bearer ";
  private static final String TOKEN_INVALID = "Token Invalid/Expired";
  private final JwtGenerator jwtGenerator;
  private final TokenBlacklistingService tokenBlacklistingService;
  private final OnsiteOrderService onsiteOrderService;

  public OrderController(JwtGenerator jwtGenerator,
      TokenBlacklistingService tokenBlacklistingService, OnsiteOrderService onsiteOrderService) {
    this.jwtGenerator = jwtGenerator;
    this.tokenBlacklistingService = tokenBlacklistingService;
    this.onsiteOrderService = onsiteOrderService;
  }

  @GetMapping("/getOrders")
  public ResponseEntity<?> getAllOrders(@RequestHeader("Authorization") String header){
    logger.info("getting all onsite orders");
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    return ResponseEntity.ok(onsiteOrderService.getAllOrders(token));
  }

  @GetMapping(value = "/getOrder/{orderId}")
  public ResponseEntity<?> getCart(@RequestHeader("Authorization") String header, @PathVariable("orderId") String orderId){
    logger.info("getting cart");
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    OnsiteOrderDto dto = onsiteOrderService.getOrder(token, orderId);

    if(dto == null){
      return ResponseEntity.ok(new ApiResponse("No order yet", HttpStatus.OK));
    }

    return ResponseEntity.ok(dto);
  }

  @GetMapping(value = "/subtractItemQuantity/{itemId}")
  public ResponseEntity<?> subtractItemQuantityInOrder(@RequestHeader("Authorization") String header, @PathVariable("itemId") String itemId){
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    return onsiteOrderService.subtractOrderItemQuantity(token, itemId);
  }

  @GetMapping(value = "/addItemQuantity/{itemId}")
  public ResponseEntity<?> addItemQuantityInOrder(@RequestHeader("Authorization") String header, @PathVariable("itemId") String itemId){
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    return onsiteOrderService.addOrderItemQuantity(token, itemId);
  }

  @GetMapping(value = "/checkout")
  public ResponseEntity<?> checkoutOrder(@RequestHeader("Authorization") String header){
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);
    onsiteOrderService.checkout(token);

    return ResponseEntity.ok(new ApiResponse("Order checkout successfully", HttpStatus.OK));
  }

  @PostMapping(value = "/addItem")
  public ResponseEntity<?> addItemToOrder(@RequestHeader("Authorization") String header, @RequestBody
  OrderForm form){
    logger.info("adding item {}", form);
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    return ResponseEntity.ok(new ApiObjectResponse(HttpStatus.CREATED, "Item added to cart", onsiteOrderService.addOrderItem(token, form)));
  }

  @DeleteMapping(value = "/removeItem/{itemId}")
  public ResponseEntity<?> removeItemInOrder(@PathVariable("itemId") String id, @RequestHeader("Authorization") String header){
    logger.info("removing item {}", id);
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    onsiteOrderService.removeOrderItem(token, id);

    logger.info("Item removed from the order");
    return ResponseEntity.ok(new ApiResponse("Item removed from the order", HttpStatus.OK));
  }

  private void validateTokenValidity(String token){
    if(!jwtGenerator.isTokenValid(token) || token.isEmpty() || tokenBlacklistingService.isTokenBlacklisted(token)){
      throw new TokenException(TOKEN_INVALID);
    }
  }
}
