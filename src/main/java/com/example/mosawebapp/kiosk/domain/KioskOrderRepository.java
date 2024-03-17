package com.example.mosawebapp.kiosk.domain;

import com.example.mosawebapp.cart.domain.Cart;
import com.example.mosawebapp.cart.domain.CartItem;
import com.example.mosawebapp.product.threadtype.domain.ThreadType;
import com.example.mosawebapp.product.threadtypedetails.domain.ThreadTypeDetails;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface KioskOrderRepository extends JpaRepository<KioskOrder, String>,
    JpaSpecificationExecutor {

  List<KioskOrder> findByKiosk(Kiosk kiosk);
  @Query("SELECT od FROM KioskOrder od WHERE od.kiosk = :kiosk")
  KioskOrder findOrderByKiosk(@Param("kiosk") Kiosk kiosk);
  @Query("SELECT CASE WHEN COUNT(od) > 0 THEN true ELSE false END FROM KioskOrder od WHERE od.kiosk = :kiosk AND od.id = :orderId")
  boolean isKioskOrderInCurrentCart(@Param("kiosk") Kiosk kiosk, @Param("orderId") String orderId);

  @Query("SELECT od FROM KioskOrder od WHERE od.kiosk = :kiosk AND od.type = :type AND od.details = :details")
  KioskOrder isKioskOrderFormAlreadyInCurrentCart(@Param("kiosk") Kiosk kiosk, @Param("type") ThreadType type, @Param("details")
  ThreadTypeDetails details);
}
