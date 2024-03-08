package com.example.mosawebapp.account.controller;

import com.example.mosawebapp.account.domain.UserRole;
import com.example.mosawebapp.account.dto.*;
import com.example.mosawebapp.account.registration.dto.AccountRegistrationDto;
import com.example.mosawebapp.account.registration.service.AccountRegistrationService;
import com.example.mosawebapp.apiresponse.ApiErrorResponse;
import com.example.mosawebapp.apiresponse.ApiObjectResponse;
import com.example.mosawebapp.exceptions.NotFoundException;
import com.example.mosawebapp.exceptions.SecurityException;
import com.example.mosawebapp.exceptions.TokenException;
import com.example.mosawebapp.account.domain.Account;
import com.example.mosawebapp.account.service.AccountService;
import com.example.mosawebapp.apiresponse.ApiResponse;
import com.example.mosawebapp.exceptions.ValidationException;
import com.example.mosawebapp.security.JwtGenerator;
import com.example.mosawebapp.security.domain.TokenBlacklistingService;
import com.example.mosawebapp.utils.DateTimeFormatter;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
@RequestMapping("/api/account")
public class AccountController {
  private static final Logger logger = LoggerFactory.getLogger(AccountController.class);
  private static final String BEARER = "Bearer ";
  private static final String TOKEN_INVALID = "Token Invalid/Expired";
  private static final String VALID = "valid";
  private static final String NOT_VALID = "not valid";
  private static final String NUMERIC_INPUTS_ONLY = "Numeric Inputs Only";
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

  @CrossOrigin(origins = {"http://localhost:5173/", "http://localhost:8080"})
  @GetMapping(value="/getAccounts")
  public ResponseEntity<?> getAccounts(@RequestHeader("Authorization") String header){
    logger.info("getting all accounts");
    String token = header.replace(BEARER, "");

    try{
      validateTokenValidity(token);

      return ResponseEntity.ok(AccountDto.buildFromEntities(accountService.findAllAccounts(token)));
    } catch(SecurityException se){
        return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"500", HttpStatus.INTERNAL_SERVER_ERROR, se.getMessage()),
          HttpStatus.INTERNAL_SERVER_ERROR);
    } catch(NotFoundException | NullPointerException ne){
        return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"400", HttpStatus.NOT_FOUND, ne.getMessage()),
          HttpStatus.BAD_REQUEST);
    } catch(TokenException te){
        return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"401", HttpStatus.UNAUTHORIZED, te.getMessage()),
          HttpStatus.UNAUTHORIZED);
    } catch(ValidationException ve){
        return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"400", HttpStatus.BAD_REQUEST, ve.getMessage()),
          HttpStatus.BAD_REQUEST);
    }
  }

  @CrossOrigin(origins = {"http://localhost:5173/", "http://localhost:8080"})
  @GetMapping(value="/getAccount/{accId}")
  public ResponseEntity<?> getAccount(@PathVariable("accId") String id, @RequestHeader("Authorization") String header){
    logger.info("getting account with id {}", id);
    String token = header.replace(BEARER, "");

    try{
      validateTokenValidity(token);

      AccountDto dto = AccountDto.buildFromEntity(accountService.findOne(id, token));

      logger.info("done fetching account for {}", dto.getEmail());
      return ResponseEntity.ok(dto);
    } catch(SecurityException se){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"500", HttpStatus.INTERNAL_SERVER_ERROR, se.getMessage()),
          HttpStatus.INTERNAL_SERVER_ERROR);
    } catch(NotFoundException | NullPointerException ne){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"404", HttpStatus.NOT_FOUND, ne.getMessage()),
          HttpStatus.BAD_REQUEST);
    } catch(TokenException te){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"401", HttpStatus.UNAUTHORIZED, te.getMessage()),
          HttpStatus.UNAUTHORIZED);
    } catch(ValidationException ve){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"400", HttpStatus.BAD_REQUEST, ve.getMessage()),
          HttpStatus.BAD_REQUEST);
    }
  }

  @CrossOrigin(origins = {"http://localhost:5173/", "http://localhost:8080"})
  @GetMapping(value="/logout")
  public ResponseEntity<?> logout(@RequestHeader("Authorization") String header){
    String token = header.replace(BEARER, "");

    try{
      if(!jwtGenerator.isTokenValid(token)){
        throw new TokenException(TOKEN_INVALID);
      }

      if(tokenBlacklistingService.isTokenBlacklisted(token)) {
        throw new SecurityException("Token can no longer be used");
      }

      tokenBlacklistingService.addTokenToBlacklist(token);

      logger.info("user logged out");
      return ResponseEntity.ok(new ApiResponse("Logged out successfully", HttpStatus.OK));
    } catch(SecurityException se){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"500", HttpStatus.INTERNAL_SERVER_ERROR, se.getMessage()),
          HttpStatus.INTERNAL_SERVER_ERROR);
    } catch(NotFoundException | NullPointerException ne){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"404", HttpStatus.NOT_FOUND, ne.getMessage()),
          HttpStatus.BAD_REQUEST);
    } catch(TokenException te){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"401", HttpStatus.UNAUTHORIZED, te.getMessage()),
          HttpStatus.UNAUTHORIZED);
    } catch(ValidationException ve){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"400", HttpStatus.BAD_REQUEST, ve.getMessage()),
          HttpStatus.BAD_REQUEST);
    }
  }

  @CrossOrigin(origins = {"http://localhost:5173/", "http://localhost:8080"})
  @GetMapping(value="/currentUser")
  public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String header){
    String token = header.replace(BEARER, "");

    try{
      validateTokenValidity(token);

      String accId = jwtGenerator.getUserFromJWT(token);
      Account account= accountService.findOne(accId, token);

      return ResponseEntity.ok(AccountDto.buildFromEntity(account));
    } catch(SecurityException se){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"500", HttpStatus.INTERNAL_SERVER_ERROR, se.getMessage()),
          HttpStatus.INTERNAL_SERVER_ERROR);
    } catch(NotFoundException | NullPointerException ne){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"404", HttpStatus.NOT_FOUND, ne.getMessage()),
          HttpStatus.BAD_REQUEST);
    } catch(TokenException te){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"401", HttpStatus.UNAUTHORIZED, te.getMessage()),
          HttpStatus.UNAUTHORIZED);
    } catch(ValidationException ve){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"400", HttpStatus.BAD_REQUEST, ve.getMessage()),
          HttpStatus.BAD_REQUEST);
    }
  }

  @CrossOrigin(origins = {"http://localhost:5173/", "http://localhost:8080"})
  @PostMapping(value="/addAccount")
  public ResponseEntity<?> createAccount(@RequestBody AccountForm form, @RequestHeader("Authorization") String header){
    logger.info("creating account with form {}", form);
    String token = header.replace(BEARER, "");

    try {
      validateTokenValidity(token);

      AccountDto dto = AccountDto.buildFromEntity(accountService.createAccount(form, token));

      logger.info("account created for {}", dto.getFullName());
      return ResponseEntity.ok(new ApiObjectResponse(HttpStatus.CREATED, "Account created for " + dto.getEmail(), dto));
    } catch(SecurityException se){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"500", HttpStatus.INTERNAL_SERVER_ERROR, se.getMessage()),
          HttpStatus.INTERNAL_SERVER_ERROR);
    } catch(NotFoundException | NullPointerException ne){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"404", HttpStatus.NOT_FOUND, ne.getMessage()),
          HttpStatus.BAD_REQUEST);
    } catch(TokenException te){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"401", HttpStatus.UNAUTHORIZED, te.getMessage()),
          HttpStatus.UNAUTHORIZED);
    } catch(ValidationException ve){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"400", HttpStatus.BAD_REQUEST, ve.getMessage()),
          HttpStatus.BAD_REQUEST);
    }
  }

  @CrossOrigin(origins = {"http://localhost:5173/", "http://localhost:8080"})
  @PostMapping(value="/register")
  public ResponseEntity<?> register(@RequestBody AccountForm form){
    logger.info("creating account for customer with form {}", form);

    try {
      form.setUserRole(UserRole.CUSTOMER);

      AccountRegistrationDto dto= AccountRegistrationDto.buildFromEntity(registrationService.register(form));

      logger.info("account created for {}", dto.getFullName());
      return ResponseEntity.ok(new ApiObjectResponse(HttpStatus.CREATED, "Account registered for " + dto.getEmail(), dto));
    } catch(SecurityException se){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"500", HttpStatus.INTERNAL_SERVER_ERROR, se.getMessage()),
          HttpStatus.INTERNAL_SERVER_ERROR);
    } catch(NotFoundException | NullPointerException ne){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"404", HttpStatus.NOT_FOUND, ne.getMessage()),
          HttpStatus.BAD_REQUEST);
    } catch(TokenException te){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"401", HttpStatus.UNAUTHORIZED, te.getMessage()),
          HttpStatus.UNAUTHORIZED);
    } catch(ValidationException ve){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"400", HttpStatus.BAD_REQUEST, ve.getMessage()),
          HttpStatus.BAD_REQUEST);
    }
  }

  @CrossOrigin(origins = {"http://localhost:5173/", "http://localhost:8080"})
  @PostMapping(value="/registerOtp/{accId}")
  public ResponseEntity<?> registerOtp(@PathVariable("accId") String id, @RequestBody OtpForm form){
    try {
      Account account = registrationService.isRegisterOtpValid(id, form.getOtp());

      if(account == null){
        throw new ValidationException("OTP Incorrect. Please Try Again");
      }

      AccountDto dto = AccountDto.buildFromEntity(account);
      return ResponseEntity.ok(new ApiObjectResponse(HttpStatus.CREATED, "Account created for " + dto.getEmail(), dto));
    } catch(SecurityException se){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"500", HttpStatus.INTERNAL_SERVER_ERROR, se.getMessage()),
          HttpStatus.INTERNAL_SERVER_ERROR);
    } catch(NotFoundException | NullPointerException ne){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"404", HttpStatus.NOT_FOUND, ne.getMessage()),
          HttpStatus.BAD_REQUEST);
    } catch(TokenException te){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"401", HttpStatus.UNAUTHORIZED, te.getMessage()),
          HttpStatus.UNAUTHORIZED);
    } catch(ValidationException ve){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"400", HttpStatus.BAD_REQUEST, ve.getMessage()),
          HttpStatus.BAD_REQUEST);
    } catch(NumberFormatException ne){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"400", HttpStatus.BAD_REQUEST, NUMERIC_INPUTS_ONLY),
          HttpStatus.BAD_REQUEST);
    }
  }

  @CrossOrigin(origins = {"http://localhost:5173/", "http://localhost:8080"})
  @PostMapping(value="/login")
  public ResponseEntity<?> login(@RequestBody LoginForm loginForm){
    try{
      logger.info("user {} attempting to login", loginForm.getEmail());

      return accountService.login(loginForm);
    } catch(SecurityException se){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"500", HttpStatus.INTERNAL_SERVER_ERROR, se.getMessage()),
          HttpStatus.INTERNAL_SERVER_ERROR);
    } catch(NotFoundException | NullPointerException ne){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"404", HttpStatus.NOT_FOUND, ne.getMessage()),
          HttpStatus.BAD_REQUEST);
    } catch(TokenException te){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"401", HttpStatus.UNAUTHORIZED, te.getMessage()),
          HttpStatus.UNAUTHORIZED);
    } catch(ValidationException ve){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"400", HttpStatus.BAD_REQUEST, ve.getMessage()),
          HttpStatus.BAD_REQUEST);
    }
  }

  @CrossOrigin(origins = {"http://localhost:5173/", "http://localhost:8080"})
  @PostMapping(value="/loginOtp/{accId}")
  public ResponseEntity<?> loginOTP(@PathVariable("accId") String id, @RequestBody OtpForm form){
    logger.info("validating login otp for account {}", id);

    try{
      boolean isValid = accountService.isOtpCorrect(id, form.getOtp(), "login");

      logger.info("Otp is {} for logging in", isValid ? VALID : NOT_VALID);
      return ResponseEntity.ok(new ApiResponse(String.valueOf(isValid), HttpStatus.OK));
    } catch(SecurityException se){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"500", HttpStatus.INTERNAL_SERVER_ERROR, se.getMessage()),
          HttpStatus.INTERNAL_SERVER_ERROR);
    } catch(NotFoundException | NullPointerException ne){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"404", HttpStatus.NOT_FOUND, ne.getMessage()),
          HttpStatus.BAD_REQUEST);
    } catch(TokenException te){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"401", HttpStatus.UNAUTHORIZED, te.getMessage()),
          HttpStatus.UNAUTHORIZED);
    } catch(ValidationException ve){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"400", HttpStatus.BAD_REQUEST, ve.getMessage()),
          HttpStatus.BAD_REQUEST);
    } catch(NumberFormatException ne){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"400", HttpStatus.BAD_REQUEST, NUMERIC_INPUTS_ONLY),
          HttpStatus.BAD_REQUEST);
    }
  }

  @CrossOrigin(origins = {"http://localhost:5173/", "http://localhost:8080"})
  @PostMapping(value="/forgotPassword")
  public ResponseEntity<?> validateEmailForChangePassword(@RequestBody EmailForm form){
    logger.info("validating email {} for password reset", form.getEmail());
    try{
      Account account = accountService.validateEmailForChangePassword(form.getEmail());

      return ResponseEntity.ok(AccountDto.buildFromEntity(account));
    } catch(SecurityException se){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"500", HttpStatus.INTERNAL_SERVER_ERROR, se.getMessage()),
          HttpStatus.INTERNAL_SERVER_ERROR);
    } catch(NotFoundException | NullPointerException ne){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"404", HttpStatus.NOT_FOUND, ne.getMessage()),
          HttpStatus.BAD_REQUEST);
    } catch(TokenException te){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"401", HttpStatus.UNAUTHORIZED, te.getMessage()),
          HttpStatus.UNAUTHORIZED);
    } catch(ValidationException ve){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"400", HttpStatus.BAD_REQUEST, ve.getMessage()),
          HttpStatus.BAD_REQUEST);
    }
  }

  @CrossOrigin(origins = {"http://localhost:5173/", "http://localhost:8080"})
  @GetMapping(value="/resetOtp")
  public ResponseEntity<?> resetOtp(@RequestBody ResetOtpForm form){
    String action = form.isRegister() ? "register" : "change password";
    logger.info("resetting otp for {}", action);

    try{
      String email = accountService.resetOtp(form.getId(), action);

      return ResponseEntity.ok(new ApiResponse("New OTP sent to " + email, HttpStatus.OK));
    } catch(SecurityException se){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"500", HttpStatus.INTERNAL_SERVER_ERROR, se.getMessage()),
          HttpStatus.INTERNAL_SERVER_ERROR);
    } catch(NotFoundException | NullPointerException ne){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"404", HttpStatus.NOT_FOUND, ne.getMessage()),
          HttpStatus.BAD_REQUEST);
    } catch(TokenException te){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"401", HttpStatus.UNAUTHORIZED, te.getMessage()),
          HttpStatus.UNAUTHORIZED);
    } catch(ValidationException ve){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"400", HttpStatus.BAD_REQUEST, ve.getMessage()),
          HttpStatus.BAD_REQUEST);
    } catch(NumberFormatException ne){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"400", HttpStatus.BAD_REQUEST, NUMERIC_INPUTS_ONLY),
          HttpStatus.BAD_REQUEST);
    }
  }

  @CrossOrigin(origins = {"http://localhost:5173/", "http://localhost:8080"})
  @PostMapping(value="/changePasswordOtp/{accId}")
  public ResponseEntity<?> changePasswordOtp(@PathVariable("accId") String id, @RequestBody OtpForm form){
    logger.info("validating login otp for account {}", id);

    try{
      boolean isValid = accountService.isOtpCorrect(id, form.getOtp(), "password change");

      if(!isValid){
        throw new ValidationException("OTP Incorrect. Try Again");
      }

      logger.info("Otp is {} for password change", VALID);
      return ResponseEntity.ok(new ApiResponse(String.valueOf(true), HttpStatus.OK));
    } catch(SecurityException se){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"500", HttpStatus.INTERNAL_SERVER_ERROR, se.getMessage()),
          HttpStatus.INTERNAL_SERVER_ERROR);
    } catch(NotFoundException | NullPointerException ne){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"404", HttpStatus.NOT_FOUND, ne.getMessage()),
          HttpStatus.BAD_REQUEST);
    } catch(TokenException te){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"401", HttpStatus.UNAUTHORIZED, te.getMessage()),
          HttpStatus.UNAUTHORIZED);
    } catch(ValidationException ve){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"400", HttpStatus.BAD_REQUEST, ve.getMessage()),
          HttpStatus.BAD_REQUEST);
    } catch(NumberFormatException ne){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"400", HttpStatus.BAD_REQUEST, NUMERIC_INPUTS_ONLY),
          HttpStatus.BAD_REQUEST);
    }
  }

  @CrossOrigin(origins = {"http://localhost:5173/", "http://localhost:8080"})
  @PostMapping(value="/changePassword/{accId}")
  public ResponseEntity<?> changePassword(@PathVariable("accId") String id, @RequestBody ChangePasswordForm form){
    logger.info("changing password for account {}", id);

    try{
      boolean withOldPassword = form.getOldPassword() != null;

      accountService.changePassword(id, form, withOldPassword);

      return ResponseEntity.ok(new ApiResponse("Password successfully changed", HttpStatus.OK));
    } catch(SecurityException se){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"500", HttpStatus.INTERNAL_SERVER_ERROR, se.getMessage()),
          HttpStatus.INTERNAL_SERVER_ERROR);
    } catch(NotFoundException | NullPointerException ne){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"404", HttpStatus.NOT_FOUND, ne.getMessage()),
          HttpStatus.BAD_REQUEST);
    } catch(TokenException te){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"401", HttpStatus.UNAUTHORIZED, te.getMessage()),
          HttpStatus.UNAUTHORIZED);
    } catch(ValidationException ve){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"400", HttpStatus.BAD_REQUEST, ve.getMessage()),
          HttpStatus.BAD_REQUEST);
    }
  }

  @CrossOrigin(origins = {"http://localhost:5173/", "http://localhost:8080"})
  @PutMapping(value="/updateAccount/{accId}")
  public ResponseEntity<?> updateAccount(@PathVariable("accId") String id, @RequestBody AccountUpdateForm form, @RequestHeader("Authorization") String header){
    logger.info("updating account with form {}", form);
    String token = header.replace(BEARER, "");

    try {
      validateTokenValidity(token);

      AccountDto dto = AccountDto.buildFromEntity(accountService.updateAccount(id, token, "admin_update", form));

      logger.info("account updated for {}", dto.getFullName());
      return ResponseEntity.ok(new ApiObjectResponse(HttpStatus.OK, "Account updated for " + dto.getEmail(), dto));
    } catch(SecurityException se){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"500", HttpStatus.INTERNAL_SERVER_ERROR, se.getMessage()),
          HttpStatus.INTERNAL_SERVER_ERROR);
    } catch(NotFoundException | NullPointerException ne){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"404", HttpStatus.NOT_FOUND, ne.getMessage()),
          HttpStatus.BAD_REQUEST);
    } catch(TokenException te){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"401", HttpStatus.UNAUTHORIZED, te.getMessage()),
          HttpStatus.UNAUTHORIZED);
    } catch(ValidationException ve){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"400", HttpStatus.BAD_REQUEST, ve.getMessage()),
          HttpStatus.BAD_REQUEST);
    }
  }

  @CrossOrigin(origins = {"http://localhost:5173/", "http://localhost:8080"})
  @PutMapping(value="/updateMyAccount")
  public ResponseEntity<?> updateMyAccount(@RequestBody AccountUpdateForm form, @RequestHeader("Authorization") String header){
    logger.info("updating account with form {}", form);
    String token = header.replace(BEARER, "");

    try {
      validateTokenValidity(token);
      String id = jwtGenerator.getUserFromJWT(token);

      AccountDto dto = AccountDto.buildFromEntity(accountService.updateAccount(id, token, "", form));

      logger.info("account updated for {}", dto.getFullName());
      return ResponseEntity.ok(dto);
    } catch(SecurityException se){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"500", HttpStatus.INTERNAL_SERVER_ERROR, se.getMessage()),
          HttpStatus.INTERNAL_SERVER_ERROR);
    } catch(NotFoundException | NullPointerException ne){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"404", HttpStatus.NOT_FOUND, ne.getMessage()),
          HttpStatus.BAD_REQUEST);
    } catch(TokenException te){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"401", HttpStatus.UNAUTHORIZED, te.getMessage()),
          HttpStatus.UNAUTHORIZED);
    } catch(ValidationException ve){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"400", HttpStatus.BAD_REQUEST, ve.getMessage()),
          HttpStatus.BAD_REQUEST);
    }
  }

  @CrossOrigin(origins = {"http://localhost:5173/", "http://localhost:8080"})
  @DeleteMapping(value="/deleteAccount/{accId}")
  public ResponseEntity<?> deleteAccount(@PathVariable("accId") String id, @RequestHeader("Authorization") String header){
    String token = header.replace(BEARER, "");

    try{
      validateTokenValidity(token);

      accountService.deleteAccount(id, token, "admin_delete");

      logger.info("done deleting account");
      return new ResponseEntity<>(new ApiResponse("Account Deleted Successfully", HttpStatus.OK), HttpStatus.OK);
    } catch(SecurityException se){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"500", HttpStatus.INTERNAL_SERVER_ERROR, se.getMessage()),
          HttpStatus.INTERNAL_SERVER_ERROR);
    } catch(NotFoundException | NullPointerException ne){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"404", HttpStatus.NOT_FOUND, ne.getMessage()),
          HttpStatus.BAD_REQUEST);
    } catch(TokenException te){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"401", HttpStatus.UNAUTHORIZED, te.getMessage()),
          HttpStatus.UNAUTHORIZED);
    } catch(ValidationException ve){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"400", HttpStatus.BAD_REQUEST, ve.getMessage()),
          HttpStatus.BAD_REQUEST);
    }
  }

  @CrossOrigin(origins = {"http://localhost:5173/", "http://localhost:8080"})
  @DeleteMapping(value="/deleteMyAccount")
  public ResponseEntity<?> deleteMyAccount(@RequestHeader("Authorization") String header){
    String token = header.replace(BEARER, "");

    try{
      validateTokenValidity(token);
      String id = jwtGenerator.getUserFromJWT(token);

      accountService.deleteAccount(id,  token, "");

      logger.info("done deleting account");
      return new ResponseEntity<>(new ApiResponse("Account Deleted Successfully", HttpStatus.OK), HttpStatus.OK);
    } catch(SecurityException se){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"500", HttpStatus.INTERNAL_SERVER_ERROR, se.getMessage()),
          HttpStatus.INTERNAL_SERVER_ERROR);
    } catch(NotFoundException | NullPointerException ne){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"404", HttpStatus.NOT_FOUND, ne.getMessage()),
          HttpStatus.BAD_REQUEST);
    } catch(TokenException te){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"401", HttpStatus.UNAUTHORIZED, te.getMessage()),
          HttpStatus.UNAUTHORIZED);
    } catch(ValidationException ve){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"400", HttpStatus.BAD_REQUEST, ve.getMessage()),
          HttpStatus.BAD_REQUEST);
    }
  }

  private void validateTokenValidity(String token){
    if(!jwtGenerator.isTokenValid(token) || token.isEmpty() || tokenBlacklistingService.isTokenBlacklisted(token)){
      throw new TokenException(TOKEN_INVALID);
    }
  }
}
