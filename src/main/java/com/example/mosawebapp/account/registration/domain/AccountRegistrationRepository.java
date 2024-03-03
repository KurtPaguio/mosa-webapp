package com.example.mosawebapp.account.registration.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRegistrationRepository extends JpaRepository<AccountRegistration, String> {

}
