package com.example.mosawebapp.controlleradvice;

import com.example.mosawebapp.apiresponse.ApiErrorResponse;
import com.example.mosawebapp.exceptions.NotFoundException;
import com.example.mosawebapp.exceptions.SecurityException;
import com.example.mosawebapp.exceptions.TokenException;
import com.example.mosawebapp.exceptions.ValidationException;
import com.example.mosawebapp.utils.DateTimeFormatter;
import java.io.IOException;
import java.util.Date;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(SecurityException.class)
  public ResponseEntity<?> handleSecurityException(SecurityException se) {
    return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"500", HttpStatus.INTERNAL_SERVER_ERROR, se.getMessage()),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(java.lang.SecurityException.class)
  public ResponseEntity<?> handleSecurityException(java.lang.SecurityException se) {
    return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"500", HttpStatus.INTERNAL_SERVER_ERROR, se.getMessage()),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ie) {
    return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"500", HttpStatus.INTERNAL_SERVER_ERROR, ie.getMessage()),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(IOException.class)
  public ResponseEntity<?> handleIOException(IOException ioe) {
    return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"500", HttpStatus.INTERNAL_SERVER_ERROR, ioe.getMessage()),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<?> handleNotFoundException(NotFoundException ne) {
    return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"404", HttpStatus.BAD_REQUEST, ne.getMessage()),
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(NullPointerException.class)
  public ResponseEntity<?> handleNullPointerException(NullPointerException ne) {
    return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"404", HttpStatus.BAD_REQUEST, ne.getMessage()),
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<?> handleValidationException(ValidationException ve) {
    return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"400", HttpStatus.BAD_REQUEST, ve.getMessage()),
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(TokenException.class)
  public ResponseEntity<?> handleTokenException(TokenException te) {
    return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"401", HttpStatus.UNAUTHORIZED, te.getMessage()),
        HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(NumberFormatException.class)
  public ResponseEntity<?> handleNumberFormatException(NumberFormatException ne) {
    return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"400", HttpStatus.BAD_REQUEST, ne.getMessage()),
        HttpStatus.BAD_REQUEST);
  }
}
