package com.example.mosawebapp.onsite_order.service;

import com.example.mosawebapp.onsite_order.dto.OnsiteOrderCheckoutDto;
import com.example.mosawebapp.onsite_order.dto.OnsiteOrderDto;
import com.example.mosawebapp.onsite_order.dto.OnsiteOrderItemDto;
import com.example.mosawebapp.onsite_order.dto.OrderForm;
import java.util.List;
import org.springframework.http.ResponseEntity;

public interface OnsiteOrderService {
  List<OnsiteOrderCheckoutDto> getAllOrders(String token);
  OnsiteOrderDto getOrder(String token, String orderId);
  OnsiteOrderItemDto addOrderItem(String token, OrderForm form);
  void removeOrderItem(String token, String itemId);
  ResponseEntity<?> subtractOrderItemQuantity(String token, String itemId);
  ResponseEntity<?> addOrderItemQuantity(String token, String itemId);
  void checkout(String token);
}
