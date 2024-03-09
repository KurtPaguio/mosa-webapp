package com.example.mosawebapp.cart.domain;

import com.example.mosawebapp.account.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, String>, JpaSpecificationExecutor {
  Cart findByAccount(Account account);
  @Query("SELECT c FROM Cart c WHERE c.account = :account AND c.isActive = :isActive ORDER BY c.dateCreated DESC")
  Cart findByAccountAndIsActiveLatest(@Param("account") Account account, @Param("isActive") boolean isActive);
}
