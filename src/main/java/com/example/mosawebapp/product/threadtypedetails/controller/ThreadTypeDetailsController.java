package com.example.mosawebapp.product.threadtypedetails.controller;

import com.example.mosawebapp.apiresponse.ApiErrorResponse;
import com.example.mosawebapp.apiresponse.ApiObjectResponse;
import com.example.mosawebapp.apiresponse.ApiResponse;
import com.example.mosawebapp.exceptions.NotFoundException;
import com.example.mosawebapp.exceptions.SecurityException;
import com.example.mosawebapp.exceptions.TokenException;
import com.example.mosawebapp.exceptions.ValidationException;
import com.example.mosawebapp.product.threadtypedetails.dto.ThreadTypeDetailsDto;
import com.example.mosawebapp.product.threadtypedetails.dto.ThreadTypeDetailsForm;
import com.example.mosawebapp.product.threadtypedetails.service.ThreadTypeDetailsService;
import com.example.mosawebapp.security.JwtGenerator;
import com.example.mosawebapp.security.domain.TokenBlacklistingService;
import com.example.mosawebapp.utils.DateTimeFormatter;
import java.util.Date;
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
@RequestMapping("/api/threadTypeDetails")
public class ThreadTypeDetailsController {
  private static final String BEARER = "Bearer ";
  private static final String TOKEN_INVALID = "Token Invalid/Expired";
  private static final String VALID = "valid";
  private static final String NOT_VALID = "not valid";
  private static final String NUMERIC_INPUTS_ONLY = "Numeric Inputs Only";
  private final TokenBlacklistingService tokenBlacklistingService;
  private final JwtGenerator jwtGenerator;
  private final ThreadTypeDetailsService threadTypeDetailsService;

  public ThreadTypeDetailsController(TokenBlacklistingService tokenBlacklistingService,
      JwtGenerator jwtGenerator, ThreadTypeDetailsService threadTypeDetailsService) {
    this.tokenBlacklistingService = tokenBlacklistingService;
    this.jwtGenerator = jwtGenerator;
    this.threadTypeDetailsService = threadTypeDetailsService;
  }

  @GetMapping(value = "/getAllDetails")
  public ResponseEntity<?> getAllThreadTypesDetails(@RequestHeader("Authorization") String header){
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    return ResponseEntity.ok(threadTypeDetailsService.findAllThreadTypesDetails());
  }

  @GetMapping(value = "/getDetails/{id}")
  public ResponseEntity<?> getThreadTypeDetails(@RequestHeader("Authorization") String header, @PathVariable("id") String id){
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    return ResponseEntity.ok(threadTypeDetailsService.findThreadTypeDetails(id));
  }

  @PostMapping(value = "/addDetails")
  public ResponseEntity<?> addThreadTypeDetails(@RequestHeader("Authorization") String header, @RequestBody
  ThreadTypeDetailsForm form){
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    ThreadTypeDetailsDto dto = threadTypeDetailsService.addThreadTypeDetails(token, form);

    return ResponseEntity.ok(new ApiObjectResponse(HttpStatus.CREATED, "Thread Type Details for " + dto.getThreadType() + " created" , dto));
  }

  @PutMapping(value = "/updateDetails/{id}")
  public ResponseEntity<?> updateBrand(@RequestHeader("Authorization") String header, @RequestBody
    ThreadTypeDetailsForm form, @PathVariable("id") String id){
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    ThreadTypeDetailsDto dto = threadTypeDetailsService.updateThreadTypeDetails(token, id, form);

    return ResponseEntity.ok(new ApiObjectResponse(HttpStatus.OK, "Thread Type Details " + dto.getThreadType() + " updated" , dto));
  }

  @DeleteMapping(value = "/deleteDetails/{id}")
  public ResponseEntity<?> updateBrand(@RequestHeader("Authorization") String header, @PathVariable("id") String id){
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    threadTypeDetailsService.deleteThreadTypeDetails(token,id);

    return ResponseEntity.ok(new ApiResponse("Thread Type Details Deleted", HttpStatus.OK));
  }

  private void validateTokenValidity(String token){
    if(!jwtGenerator.isTokenValid(token) || token.isEmpty() || tokenBlacklistingService.isTokenBlacklisted(token)){
      throw new TokenException(TOKEN_INVALID);
    }
  }
}
