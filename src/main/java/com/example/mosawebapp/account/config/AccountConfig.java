package com.example.mosawebapp.account.config;

import com.example.mosawebapp.account.domain.Account;
import com.example.mosawebapp.account.domain.AccountRepository;
import com.example.mosawebapp.account.domain.StatusRepository;
import com.example.mosawebapp.account.domain.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AccountConfig {
  private final AccountRepository accountRepository;
  private final PasswordEncoder passwordEncoder;
  private final StatusRepository statusRepository;

  @Autowired
  public AccountConfig(AccountRepository accountRepository, PasswordEncoder passwordEncoder,
      StatusRepository statusRepository) {
    this.accountRepository = accountRepository;
    this.passwordEncoder = passwordEncoder;
    this.statusRepository = statusRepository;
  }

  @Bean
  public CommandLineRunner createAdminAccount() {
    return args -> {
      if(accountRepository.findByEmail("ktfpaguio2000@gmail.com") == null){
        Account account = new Account();
        account.setUsername("admin");
        account.setEmail("ktfpaguio2000@gmail.com");
        account.setPassword(passwordEncoder.encode("kurtp2000"));
        account.setUserRole(UserRole.ADMINISTRATOR);
        account.setStatus(statusRepository.findByStatusIgnoreCase("ACTIVE"));
        accountRepository.save(account);
      }
    };
  }
}
