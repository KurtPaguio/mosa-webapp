package com.example.mosawebapp.account.service;

import com.example.mosawebapp.account.domain.RoleRepository;
import com.example.mosawebapp.account.domain.Status;
import com.example.mosawebapp.account.domain.StatusRepository;
import com.example.mosawebapp.account.domain.UserRole;
import com.example.mosawebapp.account.dto.ChangePasswordForm;
import com.example.mosawebapp.exceptions.NotFoundException;
import com.example.mosawebapp.exceptions.ValidationException;
import com.example.mosawebapp.account.domain.Account;
import com.example.mosawebapp.account.domain.AccountRepository;
import com.example.mosawebapp.account.dto.AccountForm;
import com.example.mosawebapp.account.dto.LoginForm;
import com.example.mosawebapp.mail.MailService;
import com.example.mosawebapp.security.AuthResponseDto;
import com.example.mosawebapp.security.JwtGenerator;
import com.example.mosawebapp.validate.Validate;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Random;

@Service
public class AccountServiceImpl implements AccountService{
  private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

  private static final String ACCOUNT_NOT_EXIST = "Account does not exist";
  private static final String REGISTER = "register";
  private static final String ACTIVE = "ACTIVE";
  private final AccountRepository accountRepository;
  private final StatusRepository statusRepository;
  private final RoleRepository roleRepository;
  private final AuthenticationManager authenticationManager;
  private final PasswordEncoder passwordEncoder;
  private final JwtGenerator jwtGenerator;
  private final MailService mailService;
  private final Random rnd = new Random();

  @Autowired
  public AccountServiceImpl(AccountRepository accountRepository,
      StatusRepository statusRepository, RoleRepository roleRepository, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder,
      JwtGenerator jwtGenerator, MailService mailService) {
    this.accountRepository = accountRepository;
    this.statusRepository = statusRepository;
    this.roleRepository = roleRepository;
    this.authenticationManager = authenticationManager;
    this.passwordEncoder = passwordEncoder;
    this.jwtGenerator = jwtGenerator;
    this.mailService = mailService;
  }

  @Override
  public List<Account> findAllAccounts(String token) {
    validateIfAccountIsAdmin(token);

    return accountRepository.findAll();
  }

  @Override
  public Account findOne(String id, String token) {
    validateIfAccountIsAdmin(token);

    return accountRepository.findById(id).orElseThrow(() -> new NotFoundException(ACCOUNT_NOT_EXIST));
  }

  @Override
  @Transactional
  public Account createAccount(AccountForm form, String action, String token) {
    Validate.notNull(form);
    validateForm(form);
    validateIfAccountAlreadyExists(form.getEmail(), form.getUsername());

    if(action.equalsIgnoreCase("admin_create"))
      validateIfAccountIsAdmin(token);

    Account account = new Account(form.getUsername(), form.getFullName(), form.getEmail(), form.getContactNumber(), form.getAddress(),
        passwordEncoder.encode(form.getPassword()), form.getUserRole());
    account.setRoles(Collections.singletonList(roleRepository.findByName(form.getUserRole().name())));
    account.setStatus(statusRepository.findByStatusIgnoreCase(ACTIVE));

    if(action.equalsIgnoreCase(REGISTER)){
      long otp = rnd.nextInt(999999);
      account.setRegisterOtp(otp);
      account.setStatus(statusRepository.findByStatusIgnoreCase("FOR REGISTRATION"));
      mailService.sendEmailForRegistration(account.getEmail(), otp);
      logger.info("otp sent to {}", account.getEmail());
    }

    accountRepository.save(account);
    if(!action.equalsIgnoreCase(REGISTER))
      mailService.sendEmailForAccountCreation(form);

    return account;
  }

  @Override
  @Transactional
  public Account updateAccount(String id, String token, String action, AccountForm form){
    Validate.notNull(form);
    validateForm(form);

    if(action.equalsIgnoreCase("admin_update")){
      validateIfAccountIsAdmin(token);
    }

    Account updatedAccount = accountRepository.findById(id).orElseThrow(() -> new NotFoundException(ACCOUNT_NOT_EXIST));

    updatedAccount.setUsername(form.getUsername());
    updatedAccount.setFullName(form.getFullName());
    updatedAccount.setEmail(form.getEmail());
    updatedAccount.setContactNumber(form.getContactNumber());
    updatedAccount.setAddress(form.getAddress());
    //updatedAccount.setPassword(passwordEncoder.encode(form.getPassword())); Can be changed in Change Password Feature
    updatedAccount.setUserRole(form.getUserRole());
    updatedAccount.setRoles(Collections.singletonList(roleRepository.findByName(form.getUserRole().name())));

    accountRepository.save(updatedAccount);

    return updatedAccount;
  }

  @Override
  @Transactional
  public void deleteAccount(String id, String token, String action){
    Account account = accountRepository.findById(id).orElseThrow(() -> new NotFoundException(ACCOUNT_NOT_EXIST));

    if(action.equalsIgnoreCase("admin_delete")){
      validateIfAccountIsAdmin(token);
    }

    logger.info("deleting account of {}", account.getUsername());

    accountRepository.delete(account);
  }

  @Override
  public ResponseEntity<AuthResponseDto> login(LoginForm form){
    Validate.notNull(form);
    logger.info("Logging user {} into the system", form.getUsername());

    Account account = accountRepository.findByUsername(form.getUsername());

    if(account == null){
      throw new ValidationException(ACCOUNT_NOT_EXIST);
    }

    boolean isPasswordSame = passwordEncoder.matches(form.getPassword(), account.getPassword());
    if(!isPasswordSame){
      throw new ValidationException("Password is incorrect");
    }

    /*Set login otp for the account and save to database
    long otp = rnd.nextInt(999999);
    account.setLoginOtp(otp);
    accountRepository.save(account);*/

    Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(account.getId(), form.getPassword()));
    SecurityContextHolder.getContext().setAuthentication(auth);
    String token = jwtGenerator.generateAccessToken(auth);

    /* Send otp to the account's email
    mailService.sendEmailForLogin(account.getEmail(), otp);*/

    logger.info("Account for {} found. Redirecting to otp form", form.getUsername());
    return new ResponseEntity<>(new AuthResponseDto(HttpStatus.OK, token, "Account for " + form.getUsername() + " exists"), HttpStatus.OK);
  }

  @Override
  @Transactional
  public boolean isOtpCorrect(String accId, String otp, String action) {
    Account account = accountRepository.findById(accId).orElseThrow(() -> new NotFoundException(ACCOUNT_NOT_EXIST));

    boolean isValid = determineActionAndOtpValidation(account, otp, action);

    if(isValid && action.equalsIgnoreCase(REGISTER)){ // For Registration
      account.setRegisterOtp(0);
      account.setStatus(statusRepository.findByStatusIgnoreCase(ACTIVE));
      mailService.sendEmailForAccountRegistration(account);
      accountRepository.save(account);
    } else if(isValid && !action.equalsIgnoreCase(REGISTER)){ // For Account Creation and Password Change
      account.setStatus(statusRepository.findByStatusIgnoreCase(ACTIVE));
      account.setRegisterOtp(0);
      account.setLoginOtp(0);
      account.setChangePasswordOtp(0);
      accountRepository.save(account);
    }

    return isValid;
  }

  private boolean determineActionAndOtpValidation(Account account, String otp, String action){
    boolean isValid = false;

    if(action.equalsIgnoreCase("login")){
      isValid = account.getLoginOtp() == Long.parseLong(otp);
    } else if(action.equalsIgnoreCase("password change")){
      isValid = account.getChangePasswordOtp() == Long.parseLong(otp);
    } else if(action.equalsIgnoreCase(REGISTER)){
      isValid = account.getRegisterOtp() == Long.parseLong(otp);
    }
    return isValid;
  }

  @Override
  @Transactional
  public Account validateEmailForChangePassword(String email){
    Account account = accountRepository.findByEmail(email);

    if(account == null){
      throw new ValidationException("Account with email " + email + " does not exist");
    }

    long otp = rnd.nextInt(999999);
    account.setChangePasswordOtp(otp);
    accountRepository.save(account);

    mailService.sendEmailForChangePassword(email, otp);
    logger.info("otp sent to {} for password reset", email);

    return account;
  }

  @Override
  @Transactional
  public void changePassword(String accId, ChangePasswordForm form, boolean withOldPassword) {
    Account account = accountRepository.findById(accId).orElseThrow(() -> new NotFoundException(ACCOUNT_NOT_EXIST));

    validatePasswordChanges(account, form, withOldPassword);

    account.setPassword(passwordEncoder.encode(form.getFinalPassword()));
    accountRepository.save(account);
  }

  @Override
  @Transactional
  public Account resetOtp(String id, String action){
    Account account = accountRepository.findById(id).orElseThrow(() -> new NotFoundException(ACCOUNT_NOT_EXIST));

    long otp = rnd.nextInt(999999);

    if(action.equalsIgnoreCase(REGISTER)){
      account.setRegisterOtp(otp);
      mailService.sendEmailForChangePassword(account.getEmail(), otp);
    } else if(action.equalsIgnoreCase("change password")){
      account.setChangePasswordOtp(otp);
      mailService.sendEmailForChangePassword(account.getEmail(), otp);
    }

    logger.info("otp sent to {} for {}", account.getEmail(), action);

    return account;
  }
  private void validatePasswordChanges(Account account, ChangePasswordForm form, boolean withOldPassword){
    if(!withOldPassword){
      boolean isPasswordSame = passwordEncoder.matches(form.getFinalPassword(), account.getPassword());

      if(!form.getNewPassword().equals(form.getFinalPassword())){
        throw new ValidationException("Passwords must be equal");
      }

      if(isPasswordSame){
        throw new ValidationException("New Password is same with the Old Password");
      }
    } else {
      if(form.getOldPassword().equals(form.getNewPassword()) || form.getOldPassword().equals(form.getFinalPassword())){
        throw new ValidationException("New Password is same with the Old Password");
      }
    }
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

  private void validateIfAccountIsAdmin(String token){
      String accId = jwtGenerator.getUserFromJWT(token);
      Account adminAccount = accountRepository.findById(accId).orElseThrow(() -> new NotFoundException(ACCOUNT_NOT_EXIST));

      if(adminAccount.getUserRole().equals(UserRole.CUSTOMER)){
        throw new ValidationException("Only Administrators have access to this feature");
      }
  }
}
