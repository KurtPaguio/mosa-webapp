package com.example.mosawebapp.all_orders.service;

import com.example.mosawebapp.all_orders.dto.OrdersDto;
import com.example.mosawebapp.all_orders.dto.OrdersReportDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface OrdersService {
  List<OrdersDto> getAllOrders(String token);

  List<OrdersDto> getCancelledOrders(String token, Pageable pageable);

  OrdersReportDto getAllFinishedOrdersAndStatistics(String token, Pageable pageable);

  String verifyOrder(String token, String orderId);
  String forPickup(String token, String orderId);

  String completeOrder(String token, String orderId);

  String orderNotVerified(String token, String orderId);

  void cancelOnlineOrder(String token, String orderId);
}
