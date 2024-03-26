package com.example.mosawebapp.kiosk.domain;

import com.example.mosawebapp.cart.domain.Cart;
import com.example.mosawebapp.product.threadtype.domain.ThreadType;
import com.example.mosawebapp.product.threadtypedetails.domain.ThreadTypeDetails;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface KioskRepository extends JpaRepository<Kiosk, String> {
  List<Kiosk> findByToken(String token);

  Kiosk findByIdAndToken(String id, String token);
  @Query("SELECT k FROM Kiosk k WHERE k.token = :kioskToken")
  Kiosk findKioskByToken(@Param("kioskToken") String kioskToken);

  @Query("SELECT MAX(k.queueingNumber) FROM Kiosk k")
  Long findLatestQueueingNumber();

  @Query("SELECT k FROM Kiosk k WHERE k.token = :token AND k.type = :threadType AND k.details = :threadTypeDetails And k.isCheckedOut = false")
  Kiosk findByTokenAndTypeAndDetailsAndNotCheckedOut(@Param("token") String token, @Param("threadType") ThreadType threadType, @Param("threadTypeDetails")
  ThreadTypeDetails threadTypeDetails);

  @Query(value = "SELECT k.* FROM kiosk k "
      + "INNER JOIN orders o ON o.kiosk_id = k.id "
      + "WHERE o.order_id = :orderId", nativeQuery = true)
  List<Kiosk> findAllKiosksByOrderId(@Param("orderId") String orderId);
}
