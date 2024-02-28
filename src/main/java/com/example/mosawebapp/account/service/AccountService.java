package com.example.mosawebapp.account.service;

import com.example.mosawebapp.account.domain.Account;
import com.example.mosawebapp.account.dto.AccountDto;
import com.example.mosawebapp.account.dto.AccountForm;
import com.example.mosawebapp.account.dto.LoginForm;
import com.example.mosawebapp.security.AuthResponseDto;
import java.util.List;
import org.springframework.http.ResponseEntity;

public interface AccountService {
  List<Account> findAllAccounts();
  Account findOne(String id);
  Account createAccount(AccountForm form);
  Account updateAccount(String id, AccountForm form);
  void deleteAccount(String id);
  ResponseEntity<AuthResponseDto> login(LoginForm form);
}
