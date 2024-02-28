package com.example.mosawebapp.security;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class AuthResponseDto {
    private HttpStatus status;
    private String remarks;
    private String accessToken;

    public AuthResponseDto(HttpStatus status, String accessToken, String remarks) {
        this.status = status;
        this.accessToken = accessToken;
        this.remarks = remarks;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }


    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
