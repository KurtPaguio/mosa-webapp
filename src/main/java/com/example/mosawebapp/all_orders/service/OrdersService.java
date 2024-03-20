package com.example.mosawebapp.all_orders.service;

import com.example.mosawebapp.all_orders.dto.OrdersDto;
import java.util.List;

public interface OrdersService {
  List<OrdersDto> getAllOrders(String token);

  OrdersDto verifyPayment(String token, String orderId);
  OrdersDto toBeDelivered(String token, String orderId);
  OrdersDto paymentNotVerified(String token, String orderId);
}
