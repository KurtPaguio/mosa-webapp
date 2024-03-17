package com.example.mosawebapp.kiosk.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KioskCheckoutRepository extends JpaRepository<KioskCheckout, String> {

}
