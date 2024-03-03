package com.example.mosawebapp.apiresponse;

import org.springframework.http.HttpStatus;

public class ApiObjectResponse {
  private HttpStatus status;
  private String message;
  private Object object;
  public ApiObjectResponse(HttpStatus status, String message, Object object){
    this.status = status;
    this.message = message;
    this.object = object;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Object getObject() {
    return object;
  }

  public void setObject(Object object) {
    this.object = object;
  }

  public HttpStatus getStatus() {
    return status;
  }

  public void setStatus(HttpStatus status) {
    this.status = status;
  }
}
