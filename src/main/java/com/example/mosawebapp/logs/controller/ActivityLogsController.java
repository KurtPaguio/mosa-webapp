package com.example.mosawebapp.logs.controller;

import com.example.mosawebapp.account.dto.AccountDto;
import com.example.mosawebapp.apiresponse.ApiErrorResponse;
import com.example.mosawebapp.exceptions.NotFoundException;
import com.example.mosawebapp.exceptions.SecurityException;
import com.example.mosawebapp.exceptions.TokenException;
import com.example.mosawebapp.exceptions.ValidationException;
import com.example.mosawebapp.logs.dto.ActivityLogsDto;
import com.example.mosawebapp.logs.service.ActivityLogsService;
import com.example.mosawebapp.security.JwtGenerator;
import com.example.mosawebapp.security.domain.TokenBlacklistingService;
import com.example.mosawebapp.utils.DateTimeFormatter;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logs")
public class ActivityLogsController {
  private static final Logger logger = LoggerFactory.getLogger(ActivityLogsController.class);

  private final ActivityLogsService activityLogsService;
  private final TokenBlacklistingService tokenBlacklistingService;
  private final JwtGenerator jwtGenerator;


  public ActivityLogsController(ActivityLogsService activityLogsService,
      TokenBlacklistingService tokenBlacklistingService, JwtGenerator jwtGenerator) {
    this.activityLogsService = activityLogsService;
    this.tokenBlacklistingService = tokenBlacklistingService;
    this.jwtGenerator = jwtGenerator;
  }

  @GetMapping(value="/allLogs")
  public ResponseEntity<?> getAllLogs(@RequestHeader("Authorization") String header){
    logger.info("getting all activity logs");
    String token = header.replace("Bearer ", "");

    try{
      validateTokenValidity(token);

      return ResponseEntity.ok(ActivityLogsDto.buildFromEntities(activityLogsService.getAllLogs(token)));
    } catch(SecurityException se){
      return new ResponseEntity<>(new ApiErrorResponse(
          DateTimeFormatter.get_MMDDYYY_Format(new Date()),"500", HttpStatus.INTERNAL_SERVER_ERROR, se.getMessage()),
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

  private void validateTokenValidity(String token){
    if(!jwtGenerator.isTokenValid(token) || token.isEmpty() || tokenBlacklistingService.isTokenBlacklisted(token)){
      throw new TokenException("Token Invalid/Expired");
    }
  }
}
