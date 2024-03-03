package com.example.mosawebapp.account.registration.domain;

import com.example.mosawebapp.account.dto.AccountDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRegistrationRepository extends JpaRepository<AccountRegistration, String>,
    JpaSpecificationExecutor<AccountRegistration> {

    AccountRegistration findByEmail(String email);
    AccountRegistration findByUsername(String username);
}
