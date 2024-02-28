package com.example.mosawebapp.account.service;

import com.example.mosawebapp.Exceptions.NotFoundException;
import com.example.mosawebapp.Exceptions.ValidationException;
import com.example.mosawebapp.account.domain.Account;
import com.example.mosawebapp.account.domain.AccountRepository;
import com.example.mosawebapp.account.domain.RoleRepository;
import com.example.mosawebapp.account.dto.AccountForm;
import com.example.mosawebapp.account.dto.LoginForm;
import com.example.mosawebapp.security.AuthResponseDto;
import com.example.mosawebapp.security.JwtGenerator;
import com.example.mosawebapp.validate.Validate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountServiceImpl implements AccountService{
  private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

  private final String ACCOUNT_NOT_EXIST = "Account does not exist";
  private final AccountRepository accountRepository;
  private final AuthenticationManager authenticationManager;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtGenerator jwtGenerator;

  public AccountServiceImpl(AccountRepository accountRepository,
      AuthenticationManager authenticationManager, RoleRepository roleRepository,
      PasswordEncoder passwordEncoder, JwtGenerator jwtGenerator) {
    this.accountRepository = accountRepository;
    this.authenticationManager = authenticationManager;
    this.roleRepository = roleRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtGenerator = jwtGenerator;
  }

  @Override
  public List<Account> findAllAccounts() {
    return accountRepository.findAll();
  }

  @Override
  public Account findOne(String id) {
    return accountRepository.findById(id).orElseThrow(() -> new NotFoundException(ACCOUNT_NOT_EXIST));
  }

  @Override
  public Account createAccount(AccountForm form) {
    Validate.notNull(form);
    validateForm(form);
    validateIfAccountAlreadyExists(form.getEmail(), form.getUsername());

    Account account = new Account(form.getUsername(), form.getFullName(), form.getEmail(), form.getContactNumber(),
        passwordEncoder.encode(form.getPassword()), form.getRole());
    accountRepository.save(account);

    return account;
  }

  @Override
  @Transactional
  public Account updateAccount(String id, AccountForm form){
    Validate.notNull(form);
    validateForm(form);

    Account updatedAccount = accountRepository.findById(id).orElseThrow(() -> new NotFoundException(ACCOUNT_NOT_EXIST));

    updatedAccount.setUsername(form.getUsername());
    updatedAccount.setFullName(form.getFullName());
    updatedAccount.setEmail(form.getEmail());
    updatedAccount.setContactNumber(form.getContactNumber());
    updatedAccount.setPassword(passwordEncoder.encode(form.getPassword()));
    updatedAccount.setRole(form.getRole());

    accountRepository.save(updatedAccount);

    return updatedAccount;
  }

  @Override
  public void deleteAccount(String id){
    Account account = accountRepository.findById(id).orElseThrow(() -> new NotFoundException(ACCOUNT_NOT_EXIST));

    logger.info("deleting account of {}", account.getUsername());

    accountRepository.delete(account);
  }

  @Override
  public ResponseEntity<AuthResponseDto> login(LoginForm form){
    Validate.notNull(form);
    logger.info("Logging user {} into the system", form.getUsername());

    Account account = accountRepository.findByUsername(form.getUsername());

    if(account == null){
      throw new ValidationException("Account does not exist");
    }

    Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(account.getId(), form.getPassword()));
    SecurityContextHolder.getContext().setAuthentication(auth);
    String token = jwtGenerator.generateAccessToken(auth);

    logger.info("Account {} successfully logged in", form.getUsername());
    return new ResponseEntity<>(new AuthResponseDto(HttpStatus.OK, token, "Logged in successfully with a created token"), HttpStatus.OK);
  }

  private void validateIfAccountAlreadyExists(String email, String username){
    logger.info("validating if account already exists");

    boolean existsByEmail = accountRepository.existsByEmail(email);
    boolean existsByUsername = accountRepository.existsByUsername(username);

    if(existsByEmail){
      throw new ValidationException("Account associated with the email already exists");
    }

    if(existsByUsername){
      throw new ValidationException("Account associated with the username already exists");
    }
  }
  private void validateForm(AccountForm form){
    logger.info("validating form");

    if(Validate.hasIntegersAndSpecialCharacters(form.getFullName())){
     throw new ValidationException("Name should only consists of letters");
    }

    if(!Validate.hasCorrectEmailFormat(form.getEmail())){
      throw new ValidationException("Email format not valid");
    }

    if(Validate.hasLettersInNumberInput(form.getContactNumber())){
      throw new ValidationException("Contact number must not have letters");
    }

    if(form.getPassword().length() < 8){
      throw new ValidationException("Password must have at least 8 characters");
    }

    if(form.getUsername().length() < 5){
      throw new ValidationException("Username must have at least 5 characters");
    }
  }
}
