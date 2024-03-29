package com.example.mosawebapp.all_orders.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
@Repository
public interface OrdersRepository extends JpaRepository<Orders, String> {

    @Query(value = "SELECT order_status FROM orders WHERE cart_id = :cartId", nativeQuery = true)
    String findOrderByCartId(@Param("cartId") String cartId);

    @Query(value = "SELECT o.* FROM orders o "
        + "INNER JOIN kiosk k ON k.id = o.kiosk_id "
        + "WHERE k.token = :token", nativeQuery = true)
    List<Orders> findOrdersByKioskToken(@Param("token") String token);

    List<Orders> findByOrderId(String orderId);
}
