package com.example.mosawebapp.account.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, String>,
    JpaSpecificationExecutor<Account> {
  Account findByEmail(String email);
  Account findByUsername(String username);

  @Query("SELECT CASE WHEN COUNT(acc) > 0 THEN true ELSE false END FROM Account acc WHERE acc.email = :email")
  boolean existsByEmail(@Param("email") String email);

  @Query("SELECT CASE WHEN COUNT(acc) > 0 THEN true ELSE false END FROM Account acc WHERE acc.username = :username")
  boolean existsByUsername(@Param("username") String username);
}
