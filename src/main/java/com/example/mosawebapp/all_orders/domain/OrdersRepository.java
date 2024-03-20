package com.example.mosawebapp.all_orders.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
@Repository
public interface OrdersRepository extends JpaRepository<Orders, String> {

    @Query(value = "SELECT order_status FROM orders WHERE id = :orderId", nativeQuery = true)
    String findOrderStatusByOrderId(@Param("orderId") String orderId);

    @Query(value = "SELECT * FROM orders WHERE :orderId = ANY(string_to_array(order_ids, ','))", nativeQuery = true)
    Orders findOrderByOrderId(@Param("orderId") String orderId);
}
