package com.example.mosawebapp.account.controller;

import com.example.mosawebapp.account.domain.UserRole;
import com.example.mosawebapp.account.dto.ChangePasswordForm;
import com.example.mosawebapp.account.dto.EmailForm;
import com.example.mosawebapp.account.dto.OtpForm;
import com.example.mosawebapp.account.registration.dto.AccountRegistrationDto;
import com.example.mosawebapp.account.registration.service.AccountRegistrationService;
import com.example.mosawebapp.exceptions.SecurityException;
import com.example.mosawebapp.exceptions.TokenException;
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
  private static final String VALID = "valid";
  private static final String NOT_VALID = "not valid";
  private final AccountService accountService;
  private final AccountRegistrationService registrationService;
  private final TokenBlacklistingService tokenBlacklistingService;
  private final JwtGenerator jwtGenerator;


  public AccountController(AccountService accountService,
      AccountRegistrationService registrationService, TokenBlacklistingService tokenBlacklistingService, JwtGenerator jwtGenerator) {
    this.accountService = accountService;
    this.registrationService = registrationService;
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

      return ResponseEntity.ok(AccountDto.buildFromEntities(accountService.findAllAccounts(token)));
    } catch (Exception e){
      logger.error(ERROR_DUE, e.getMessage());
      return new ResponseEntity<>(new ApiResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
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

      AccountDto dto = AccountDto.buildFromEntity(accountService.findOne(id, token));

      logger.info("done fetching account for {}", dto.getUsername());
      return ResponseEntity.ok(dto);
    } catch (Exception e){
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
      Account account= accountService.findOne(accId, token);

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

      AccountDto dto = AccountDto.buildFromEntity(accountService.createAccount(form, token));

      logger.info("account created for {}", dto.getFullName());
      return ResponseEntity.ok(dto);
    } catch (Exception e){
      logger.error(ERROR_DUE, e.getMessage());
      return new ResponseEntity<>(new ApiResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping(value="/register")
  public ResponseEntity<?> register(@RequestBody AccountForm form){
    logger.info("creating account for customer with form {}", form);

    try {
      form.setUserRole(UserRole.CUSTOMER);

      AccountRegistrationDto dto= AccountRegistrationDto.buildFromEntity(registrationService.register(form));

      logger.info("account created for {}", dto.getFullName());
      return ResponseEntity.ok(dto);
    } catch (Exception e){
      logger.error(ERROR_DUE, e.getMessage());
      return new ResponseEntity<>(new ApiResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping(value="/registerOtp/{accId}")
  public ResponseEntity<?> registerOtp(@PathVariable("accId") String id, @RequestBody OtpForm form){
    try {
      boolean isValid = registrationService.isRegisterOtpValid(id, form.getOtp());

      logger.info("otp is {} for registration", isValid ? VALID : NOT_VALID);
      return ResponseEntity.ok(new ApiResponse(String.valueOf(isValid)));
    } catch (Exception e){
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

  @PostMapping(value="/loginOtp/{accId}")
  public ResponseEntity<?> loginOTP(@PathVariable("accId") String id, @RequestBody OtpForm form){
    logger.info("validating login otp for account {}", id);

    try{
      boolean isValid = accountService.isOtpCorrect(id, form.getOtp(), "login");

      logger.info("Otp is {} for logging in", isValid ? VALID : NOT_VALID);
      return ResponseEntity.ok(new ApiResponse(String.valueOf(isValid)));
    } catch (Exception e){
      logger.error(ERROR_DUE, e.getMessage());
      return new ResponseEntity<>(new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping(value="/validateEmail")
  public ResponseEntity<?> validateEmailForChangePassword(@RequestBody EmailForm form){
    logger.info("validating email {} for password reset", form.getEmail());
    try{
      Account account = accountService.validateEmailForChangePassword(form.getEmail());

      return ResponseEntity.ok(AccountDto.buildFromEntity(account));
    } catch (Exception e){
      logger.error(ERROR_DUE, e.getMessage());
      return new ResponseEntity<>(new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping(value="/resetOtp/{accId}/{action}")
  public ResponseEntity<?> resetOtp(@PathVariable("accId") String id, @PathVariable("action") String action){
    logger.info("resetting otp for {}", action);

    try{
      String email = accountService.resetOtp(id, action);

      return ResponseEntity.ok(new ApiResponse("New OTP sent to " + email));
    } catch (Exception e){
      return new ResponseEntity<>(new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
  @PostMapping(value="/changePasswordOtp/{accId}")
  public ResponseEntity<?> changePasswordOtp(@PathVariable("accId") String id, @RequestBody OtpForm form){
    logger.info("validating login otp for account {}", id);

    try{
      boolean isValid = accountService.isOtpCorrect(id, form.getOtp(), "password change");

      logger.info("Otp is {} for password change", isValid ? VALID : NOT_VALID);
      return ResponseEntity.ok(new ApiResponse(String.valueOf(isValid)));
    } catch (Exception e){
      logger.error(ERROR_DUE, e.getMessage());
      return new ResponseEntity<>(new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping(value="/changePassword/{accId}")
  public ResponseEntity<?> changePassword(@PathVariable("accId") String id, @RequestBody ChangePasswordForm form){
    logger.info("changing password for account {}", id);
    try{
      boolean withOldPassword = !form.getOldPassword().isEmpty();

      accountService.changePassword(id, form, withOldPassword);

      return ResponseEntity.ok(new ApiResponse("Password successfully changed"));
    } catch (Exception e){
      logger.error(ERROR_DUE, e.getMessage());
      return new ResponseEntity<>(new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()),
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

      AccountDto dto = AccountDto.buildFromEntity(accountService.updateAccount(id, token, "admin_update", form));

      logger.info("account updated for {}", dto.getFullName());
      return ResponseEntity.ok(dto);
    } catch (Exception e){
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

      accountService.deleteAccount(id, token, "admin_delete");

      logger.info("done deleting account");
      return new ResponseEntity<>(new ApiResponse("Account Deleted Successfully"), HttpStatus.OK);
    } catch (Exception e){
      logger.error(ERROR_DUE, e.getMessage());
      return new ResponseEntity<>(new ApiResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @DeleteMapping(value="/deleteMyAccount/{accId}")
  public ResponseEntity<?> deleteMyAccount(@PathVariable("accId") String id, @RequestHeader("Authorization") String header){
    String token = header.replace(BEARER, "");

    try{
      if(!jwtGenerator.isTokenValid(token) || token.isEmpty() || tokenBlacklistingService.isTokenBlacklisted(token)){
        throw new TokenException(TOKEN_INVALID);
      }

      accountService.deleteAccount(id,  token, "");

      logger.info("done deleting account");
      return new ResponseEntity<>(new ApiResponse("Account Deleted Successfully"), HttpStatus.OK);
    } catch (Exception e){
      logger.error(ERROR_DUE, e.getMessage());
      return new ResponseEntity<>(new ApiResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
