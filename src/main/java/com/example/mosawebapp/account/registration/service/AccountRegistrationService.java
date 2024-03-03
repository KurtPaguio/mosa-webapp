package com.example.mosawebapp.account.registration.service;

import com.example.mosawebapp.account.dto.AccountForm;
import com.example.mosawebapp.account.registration.domain.AccountRegistration;

public interface AccountRegistrationService {
  AccountRegistration register(AccountForm form);
  boolean isRegisterOtpValid(String id, String otp);
}
