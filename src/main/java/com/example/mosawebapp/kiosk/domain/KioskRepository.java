package com.example.mosawebapp.kiosk.domain;

import com.example.mosawebapp.account.domain.Account;
import com.example.mosawebapp.cart.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface KioskRepository extends JpaRepository<Kiosk, String>, JpaSpecificationExecutor {
  Kiosk findByKioskNumber(String kioskNumber);

  @Query("SELECT k FROM Kiosk k WHERE k.kioskNumber = :kioskNumber AND k.isActive = :isActive ORDER BY k.dateCreated DESC")
  Kiosk findByKioskNumberAndIsActiveLatest(@Param("kioskNumber") String kioskNumber, @Param("isActive") boolean isActive);
}
