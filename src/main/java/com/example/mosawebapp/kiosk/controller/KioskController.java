package com.example.mosawebapp.kiosk.controller;

import com.example.mosawebapp.api_response.ApiObjectResponse;
import com.example.mosawebapp.api_response.ApiResponse;
import com.example.mosawebapp.exceptions.TokenException;
import com.example.mosawebapp.kiosk.dto.KioskDto;
import com.example.mosawebapp.kiosk.dto.KioskOrderForm;
import com.example.mosawebapp.kiosk.dto.KioskOrderQuantityForm;
import com.example.mosawebapp.kiosk.service.KioskService;
import com.example.mosawebapp.security.JwtGenerator;
import com.example.mosawebapp.security.domain.TokenBlacklistingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/kiosk")
public class KioskController {
  private static final Logger logger = LoggerFactory.getLogger(KioskController.class);
  private static final String BEARER = "Bearer ";
  private static final String TOKEN_INVALID = "Token Invalid/Expired";
  private final KioskService kioskService;
  private final JwtGenerator jwtGenerator;
  private final TokenBlacklistingService tokenBlacklistingService;

  @Autowired
  public KioskController(KioskService kioskService, JwtGenerator jwtGenerator,
      TokenBlacklistingService tokenBlacklistingService) {
    this.kioskService = kioskService;
    this.jwtGenerator = jwtGenerator;
    this.tokenBlacklistingService = tokenBlacklistingService;
  }

  @GetMapping(value = "/startOrder")
  public ResponseEntity<?> startOder(){
    logger.info("start kiosk ordering");

    return ResponseEntity.ok(kioskService.startOrdering());
  }
  @GetMapping(value = "/getCheckouts")
  public ResponseEntity<?> getCheckout(@RequestHeader("Authorization") String header){
    logger.info("getting kiosk checkouts");
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    return ResponseEntity.ok(kioskService.getCheckouts(token));
  }

  @GetMapping(value = "/getKioskOrder/{kioskToken}")
  public ResponseEntity<?> getKioskOrder(@PathVariable("kioskToken") String token){
    logger.info("getting kiosk order");

    KioskDto dto = kioskService.getKiosk(token);

    if(dto == null){
      return ResponseEntity.ok(new ApiResponse("No kiosk orders yet", HttpStatus.OK));
    }

    return ResponseEntity.ok(dto);
  }

  @GetMapping(value = "/subtractItemQuantity")
  public ResponseEntity<?> subtractItemQuantityInCart(@ModelAttribute KioskOrderQuantityForm form){
    return kioskService.subtractKioskOrderQuantity(form);
  }

  @GetMapping(value = "/addItemQuantity/")
  public ResponseEntity<?> addItemQuantityInCart(@ModelAttribute KioskOrderQuantityForm form){
    return kioskService.addKioskOrderQuantity(form);
  }

  @GetMapping(value = "/checkout/{token}")
  public ResponseEntity<?> checkoutKioskOrder(@PathVariable("token") String token){
    kioskService.checkout(token);

    return ResponseEntity.ok(new ApiResponse("Kiosk Order checkout successfully", HttpStatus.OK));
  }

  @PostMapping(value = "/addItem/{kioskToken}")
  public ResponseEntity<?> addItemToKiosk(@PathVariable("kioskToken") String token, @RequestBody
      KioskOrderForm form){
    logger.info("adding item {}", form);

    return ResponseEntity.ok(new ApiObjectResponse(HttpStatus.CREATED, "Item added to Kiosk Order", kioskService.addKioskOrder(token, form)));
  }

  @DeleteMapping(value = "/removeItem/")
  public ResponseEntity<?> removeItemInKioskOrder(@ModelAttribute KioskOrderQuantityForm form){
    logger.info("removing item {} in kiosk order", form.getItemId());

    kioskService.removeKioskOrder(form);

    logger.info("Item removed from the order");
    return ResponseEntity.ok(new ApiResponse("Item removed from the order", HttpStatus.OK));
  }

  private void validateTokenValidity(String token){
    if(!jwtGenerator.isTokenValid(token) || token.isEmpty() || tokenBlacklistingService.isTokenBlacklisted(token)){
      throw new TokenException(TOKEN_INVALID);
    }
  }
}
