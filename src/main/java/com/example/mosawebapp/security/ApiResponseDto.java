package com.example.mosawebapp.security;

import org.springframework.http.HttpStatus;

public class ApiResponseDto {
    private final HttpStatus httpStatus;
    private String remarks;

    public ApiResponseDto(HttpStatus httpStatus, String remarks) {
        this.httpStatus = httpStatus;
        this.remarks = remarks;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(String httpStatus) {
        httpStatus = httpStatus;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
