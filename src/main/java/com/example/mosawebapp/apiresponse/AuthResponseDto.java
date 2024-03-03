package com.example.mosawebapp.apiresponse;

import com.example.mosawebapp.account.dto.AccountDto;
import org.springframework.http.HttpStatus;

public class AuthResponseDto {
    private HttpStatus status;
    private String remarks;
    private String accessToken;
    private AccountDto account;

    public AuthResponseDto(HttpStatus status, AccountDto account, String accessToken, String remarks) {
        this.status = status;
        this.account = account;
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

    public AccountDto getAccountDto() {
        return account;
    }

    public void setAccountDto(AccountDto account) {
        this.account = account;
    }
}
