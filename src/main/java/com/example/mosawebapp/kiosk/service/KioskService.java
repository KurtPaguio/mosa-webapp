package com.example.mosawebapp.kiosk.service;

import com.example.mosawebapp.cart.dto.CartDto;
import com.example.mosawebapp.cart.dto.CartItemDto;
import com.example.mosawebapp.cart.dto.CartItemForm;
import com.example.mosawebapp.kiosk.dto.KioskCheckoutDto;
import com.example.mosawebapp.kiosk.dto.KioskDto;
import com.example.mosawebapp.kiosk.dto.KioskOrderDto;
import com.example.mosawebapp.kiosk.dto.KioskOrderForm;
import com.example.mosawebapp.kiosk.dto.KioskOrderQuantityForm;
import java.util.List;
import org.springframework.http.ResponseEntity;

public interface KioskService {
  KioskDto startOrdering();
  List<KioskCheckoutDto> getCheckouts(String adminToken);
  KioskDto getKiosk(String kioskNumber);
  KioskOrderDto addKioskOrder(String kioskNumber, KioskOrderForm form);
  void removeKioskOrder(KioskOrderQuantityForm form);
  ResponseEntity<?> subtractKioskOrderQuantity(KioskOrderQuantityForm form);
  ResponseEntity<?> addKioskOrderQuantity(KioskOrderQuantityForm form);
  void checkout(String kioskNumber);
}
