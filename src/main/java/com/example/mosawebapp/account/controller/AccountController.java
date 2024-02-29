package com.example.mosawebapp.account.controller;

import com.example.mosawebapp.Exceptions.NotFoundException;
import com.example.mosawebapp.Exceptions.TokenException;
import com.example.mosawebapp.Exceptions.ValidationException;
import com.example.mosawebapp.account.domain.Account;
import com.example.mosawebapp.account.dto.AccountDto;
import com.example.mosawebapp.account.dto.AccountForm;
import com.example.mosawebapp.account.dto.LoginForm;
import com.example.mosawebapp.account.service.AccountService;
import com.example.mosawebapp.apiresponse.ApiResponse;
import com.example.mosawebapp.security.ApiResponseDto;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/account/")
public class AccountController {
  private static final Logger logger = LoggerFactory.getLogger(AccountController.class);
  private static final String TOKEN_INVALID = "Token not already valid. Please login again";
  private static final String BEARER = "Bearer ";
  private static final String ERROR_DUE = "Error due to {}";
  private final AccountService accountService;
  private final TokenBlacklistingService tokenBlacklistingService;
  private final JwtGenerator jwtGenerator;


  public AccountController(AccountService accountService,
      TokenBlacklistingService tokenBlacklistingService, JwtGenerator jwtGenerator) {
    this.accountService = accountService;
    this.tokenBlacklistingService = tokenBlacklistingService;
    this.jwtGenerator = jwtGenerator;
  }

  @GetMapping(value="/getAccounts")
  public ResponseEntity<?> getAccounts(@RequestHeader("Authorization") String header){
    logger.info("getting all accounts");
    String token = header.replace(BEARER, "");

    try{
      if(!jwtGenerator.isTokenValid(token) || token.isEmpty() || tokenBlacklistingService.isTokenBlacklisted(token)){
        throw new TokenException(TOKEN_INVALID);
      }

      return ResponseEntity.ok(AccountDto.buildFromEntities(accountService.findAllAccounts()));
    } catch (TokenException te){
      logger.error(ERROR_DUE, te.getMessage());
      return new ResponseEntity<>(new ApiResponse(te.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping(value="/getAccount/{accId}")
  public ResponseEntity<?> getAccount(@PathVariable("accId") String id, @RequestHeader("Authorization") String header){
    logger.info("getting account with id {}", id);
    String token = header.replace(BEARER, "");

    try{
      if(!jwtGenerator.isTokenValid(token) || token.isEmpty() || tokenBlacklistingService.isTokenBlacklisted(token)){
        throw new TokenException(TOKEN_INVALID);
      }

      AccountDto dto = AccountDto.buildFromEntity(accountService.findOne(id));

      logger.info("done fetching account for {}", dto.getUsername());
      return ResponseEntity.ok(dto);
    } catch (NotFoundException | TokenException e){
      logger.error(ERROR_DUE, e.getMessage());
      return new ResponseEntity<>(new ApiResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping(value="/logout")
  public ResponseEntity<?> logout(@RequestHeader("Authorization") String header){
    String token = header.replace(BEARER, "");

    try{
      if(!jwtGenerator.isTokenValid(token)){
        throw new TokenException(TOKEN_INVALID);
      }

      if(tokenBlacklistingService.isTokenBlacklisted(token)) {
        throw new SecurityException("Token is already blacklisted");
      }

      tokenBlacklistingService.addTokenToBlacklist(token);

      logger.info("user logged out");
      return ResponseEntity.ok(new ApiResponse("Logged out successfully"));
    } catch (Exception e){
      return new ResponseEntity<>(new ApiResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping(value="/currentUser")
  public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String header){
    String token = header.replace(BEARER, "");

    try{
      if(!jwtGenerator.isTokenValid(token) || token.isEmpty() || tokenBlacklistingService.isTokenBlacklisted(token)){
        throw new TokenException(TOKEN_INVALID);
      }

      String accId = jwtGenerator.getUserFromJWT(token);
      Account account= accountService.findOne(accId);

      return ResponseEntity.ok(AccountDto.buildFromEntity(account));
    } catch (Exception e){
      logger.error(ERROR_DUE, e.getMessage());
      return new ResponseEntity<>(new ApiResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping(value="/addAccount")
  public ResponseEntity<?> createAccount(@RequestBody AccountForm form, @RequestHeader("Authorization") String header){
    logger.info("creating account with form {}", form);
    String token = header.replace(BEARER, "");

    try {
      if(!jwtGenerator.isTokenValid(token) || token.isEmpty() || tokenBlacklistingService.isTokenBlacklisted(token)){
        throw new TokenException(TOKEN_INVALID);
      }

      AccountDto dto = AccountDto.buildFromEntity(accountService.createAccount(form));

      logger.info("account created for {}", dto.getFullName());
      return ResponseEntity.ok(dto);
    } catch (ValidationException | TokenException e){
      logger.error(ERROR_DUE, e.getMessage());
      return new ResponseEntity<>(new ApiResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping(value="/login")
  public ResponseEntity<?> login(@RequestBody LoginForm loginForm){
    try{
      return accountService.login(loginForm);
    } catch (Exception e){
      logger.error("Error logging into the system: {}", e.getMessage());
      return new ResponseEntity<>(new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR, "Error logging in: " + e.getMessage()),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PutMapping(value="/updateAccount/{accId}")
  public ResponseEntity<?> updateAccount(@PathVariable("accId") String id, @RequestBody AccountForm form, @RequestHeader("Authorization") String header){
    logger.info("updating account with form {}", form);
    String token = header.replace(BEARER, "");

    try {
      if(!jwtGenerator.isTokenValid(token) || token.isEmpty() || tokenBlacklistingService.isTokenBlacklisted(token)){
        throw new TokenException(TOKEN_INVALID);
      }

      AccountDto dto = AccountDto.buildFromEntity(accountService.updateAccount(id,form));

      logger.info("account updated for {}", dto.getFullName());
      return ResponseEntity.ok(dto);
    } catch (ValidationException | NotFoundException | TokenException e){
      logger.error(ERROR_DUE, e.getMessage());
      return new ResponseEntity<>(new ApiResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @DeleteMapping(value="/deleteAccount/{accId}")
  public ResponseEntity<?> deleteAccount(@PathVariable("accId") String id, @RequestHeader("Authorization") String header){
    String token = header.replace(BEARER, "");

    try{
      if(!jwtGenerator.isTokenValid(token) || token.isEmpty() || tokenBlacklistingService.isTokenBlacklisted(token)){
        throw new TokenException(TOKEN_INVALID);
      }

      accountService.deleteAccount(id);

      logger.info("done deleting account");
      return new ResponseEntity<>(new ApiResponse("Account Deleted Successfully"), HttpStatus.OK);
    } catch (NotFoundException | TokenException e){
      logger.error(ERROR_DUE, e.getMessage());
      return new ResponseEntity<>(new ApiResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
