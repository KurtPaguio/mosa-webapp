package com.example.mosawebapp.cart.domain;

import com.example.mosawebapp.account.domain.Account;
import com.example.mosawebapp.product.threadtype.domain.ThreadType;
import com.example.mosawebapp.product.threadtypedetails.domain.ThreadTypeDetails;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, String> {
  List<Cart> findByAccount(Account account);
  @Query(value = "SELECT * FROM cart WHERE customer_id = :customerId AND is_checked_out = false", nativeQuery = true)
  List<Cart> findByAccountAndIsNotCheckedOut(@Param("customerId") String customerId);
  Cart findByIdAndAccount(String id, Account account);
  @Query("SELECT c FROM Cart c WHERE c.account = :account AND c.type = :threadType AND c.details = :threadTypeDetails ORDER BY c.dateCreated DESC")
  Cart findLatestByAccountAndTypeAndDetails(@Param("account") Account account, @Param("threadType") ThreadType threadType, @Param("threadTypeDetails")
  ThreadTypeDetails threadTypeDetails);
}
