package com.example.mosawebapp.onsite_order.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OnsiteOrderCheckoutRepository extends JpaRepository<OnsiteOrderCheckout, String> {

}
