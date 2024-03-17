package com.example.mosawebapp.all_orders.service;

import com.example.mosawebapp.account.controller.AccountController;
import com.example.mosawebapp.account.domain.Account;
import com.example.mosawebapp.account.domain.AccountRepository;
import com.example.mosawebapp.account.domain.UserRole;
import com.example.mosawebapp.all_orders.domain.Orders;
import com.example.mosawebapp.all_orders.domain.OrdersRepository;
import com.example.mosawebapp.all_orders.dto.OrdersDto;
import com.example.mosawebapp.cart.domain.Cart;
import com.example.mosawebapp.cart.domain.CartItem;
import com.example.mosawebapp.cart.domain.CartItemRepository;
import com.example.mosawebapp.cart.domain.CartRepository;
import com.example.mosawebapp.cart.dto.CartDto;
import com.example.mosawebapp.cart.service.CartService;
import com.example.mosawebapp.exceptions.NotFoundException;
import com.example.mosawebapp.exceptions.ValidationException;
import com.example.mosawebapp.kiosk.domain.Kiosk;
import com.example.mosawebapp.kiosk.domain.KioskOrder;
import com.example.mosawebapp.kiosk.domain.KioskOrderRepository;
import com.example.mosawebapp.kiosk.domain.KioskRepository;
import com.example.mosawebapp.kiosk.dto.KioskDto;
import com.example.mosawebapp.onsite_order.domain.OnsiteOrder;
import com.example.mosawebapp.onsite_order.domain.OnsiteOrderItem;
import com.example.mosawebapp.onsite_order.domain.OnsiteOrderItemRepository;
import com.example.mosawebapp.onsite_order.domain.OnsiteOrderRepository;
import com.example.mosawebapp.onsite_order.dto.OnsiteOrderDto;
import com.example.mosawebapp.security.JwtGenerator;
import com.example.mosawebapp.security.domain.TokenBlacklistingService;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrdersServiceImpl implements OrdersService {
  private final AccountRepository accountRepository;
  private final CartRepository cartRepository;
  private final CartItemRepository cartItemRepository;
  private final KioskRepository kioskRepository;
  private final KioskOrderRepository kioskOrderRepository;
  private final OnsiteOrderRepository onsiteOrderRepository;
  private final OnsiteOrderItemRepository onsiteOrderItemRepository;
  private final OrdersRepository ordersRepository;
  private final JwtGenerator jwtGenerator;
  private final TokenBlacklistingService tokenBlacklistingService;
  private static final Logger logger = LoggerFactory.getLogger(OrdersServiceImpl.class);


  @Autowired
  public OrdersServiceImpl(AccountRepository accountRepository,
      CartService cartService, CartRepository cartRepository,
      CartItemRepository cartItemRepository, KioskRepository kioskRepository,
      KioskOrderRepository kioskOrderRepository, OnsiteOrderRepository onsiteOrderRepository,
      OnsiteOrderItemRepository onsiteOrderItemRepository, OrdersRepository ordersRepository, JwtGenerator jwtGenerator,
      TokenBlacklistingService tokenBlacklistingService) {
    this.accountRepository = accountRepository;
    this.cartRepository = cartRepository;
    this.cartItemRepository = cartItemRepository;
    this.kioskRepository = kioskRepository;
    this.kioskOrderRepository = kioskOrderRepository;
    this.onsiteOrderRepository = onsiteOrderRepository;
    this.onsiteOrderItemRepository = onsiteOrderItemRepository;
    this.ordersRepository = ordersRepository;
    this.jwtGenerator = jwtGenerator;
    this.tokenBlacklistingService = tokenBlacklistingService;
  }

  @Override
  public List<OrdersDto> getAllOrders(String token) {
    validateIfAccountIsNotCustomerOrContentManager(token);

    List<Orders> orders = ordersRepository.findAll();
    List<OrdersDto> dto = new ArrayList<>();
    for(Orders order: orders){
      dto.add(convertToOrdersDto(order));
    }

    return dto;
  }

  private OrdersDto convertToOrdersDto(Orders order) {
    Cart cart = cartRepository.findById(order.getOrderId()).orElse(null);
    logger.info("Cart {}", cart != null ? cart.getId() : "NULL CART");

    if (cart != null) {
      logger.info("Cart not null");
      List<CartItem> cartItems = cartItemRepository.findByCart(cart);
      CartDto cartDto = new CartDto(cart, cart.getAccount(), cartItems);

      return new OrdersDto(order.getId(), cartDto, null, null);
    }

    Kiosk kiosk = kioskRepository.findById(order.getOrderId()).orElse(null);
    if (kiosk != null) {
      logger.info("Kiosk not null");

      List<KioskOrder> kioskOrders = kioskOrderRepository.findByKiosk(kiosk);
      KioskDto kioskDto = new KioskDto(kiosk, kioskOrders);

      return new OrdersDto(order.getId(), null, kioskDto, null);
    }

    OnsiteOrder onsiteOrder = onsiteOrderRepository.findById(order.getOrderId()).orElse(null);
    if (onsiteOrder != null) {
      logger.info("Onsite Order not null");

      List<OnsiteOrderItem> orderItems = onsiteOrderItemRepository.findByOrder(onsiteOrder);
      OnsiteOrderDto onsiteOrderDto = new OnsiteOrderDto(onsiteOrder, orderItems);

      return new OrdersDto(order.getId(), null, null, onsiteOrderDto);
    }

    return new OrdersDto(order.getId(), null, null, null);
  }


  private Account getAccountFromToken(String token){
    String id = jwtGenerator.getUserFromJWT(token);

    return accountRepository.findById(id).orElseThrow(() -> new NotFoundException("Account does not exists"));
  }

  private void validateIfAccountIsNotCustomerOrContentManager(String token){
    String accId = jwtGenerator.getUserFromJWT(token);
    Account adminAccount = accountRepository.findById(accId).orElseThrow(() -> new NotFoundException("Account does not exists"));

    if(adminAccount.getUserRole().equals(UserRole.CUSTOMER) || adminAccount.getUserRole().equals(UserRole.CONTENT_MANAGER)){
      throw new ValidationException("Only Administrators, Product, and Order Managers have access to this feature");
    }
  }
}
