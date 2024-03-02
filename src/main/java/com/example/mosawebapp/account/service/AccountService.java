package com.example.mosawebapp.account.service;

import com.example.mosawebapp.account.domain.Account;
import com.example.mosawebapp.account.dto.AccountDto;
import com.example.mosawebapp.account.dto.AccountForm;
import com.example.mosawebapp.account.dto.ChangePasswordForm;
import com.example.mosawebapp.account.dto.LoginForm;
import com.example.mosawebapp.security.AuthResponseDto;
import java.util.List;
import org.springframework.http.ResponseEntity;

public interface AccountService {
  List<Account> findAllAccounts(String token);
  Account findOne(String id, String token);
  Account createAccount(AccountForm form, String action, String token);
  Account updateAccount(String id, String token, String action, AccountForm form);
  void deleteAccount(String id, String token, String action);
  ResponseEntity<AuthResponseDto> login(LoginForm form);
  boolean isOtpCorrect(String accId, String otp, String action);
  Account validateEmailForChangePassword(String email);
  void changePassword(String accId, ChangePasswordForm form, boolean withOldPassword);
  Account resetOtp(String id, String action);
}
