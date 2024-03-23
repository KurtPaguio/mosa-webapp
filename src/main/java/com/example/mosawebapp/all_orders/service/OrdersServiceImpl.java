package com.example.mosawebapp.all_orders.service;

import com.example.mosawebapp.account.domain.Account;
import com.example.mosawebapp.account.domain.AccountRepository;
import com.example.mosawebapp.account.domain.UserRole;
import com.example.mosawebapp.all_orders.domain.OrderStatus;
import com.example.mosawebapp.all_orders.domain.OrderType;
import com.example.mosawebapp.all_orders.domain.Orders;
import com.example.mosawebapp.all_orders.domain.OrdersRepository;
import com.example.mosawebapp.all_orders.dto.OrdersDto;
import com.example.mosawebapp.cart.domain.Cart;
import com.example.mosawebapp.cart.domain.CartRepository;
import com.example.mosawebapp.cart.dto.CartDto;
import com.example.mosawebapp.exceptions.NotFoundException;
import com.example.mosawebapp.exceptions.ValidationException;
import com.example.mosawebapp.logs.service.ActivityLogsService;
import com.example.mosawebapp.mail.MailService;
import com.example.mosawebapp.product.threadtypedetails.dto.ThreadTypeDetailsDto;
import com.example.mosawebapp.security.JwtGenerator;

import java.util.*;
import java.util.stream.Collectors;
import javax.persistence.criteria.Order;
import one.util.streamex.StreamEx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrdersServiceImpl implements OrdersService{
  private static final Logger logger = LoggerFactory.getLogger(OrdersServiceImpl.class);
  private static final String ORDER_NOT_EXISTS = "Order does not exits";
  private final CartRepository cartRepository;
  private final OrdersRepository ordersRepository;
  private final AccountRepository accountRepository;
  private final JwtGenerator jwtGenerator;
  private final MailService mailService;
  private final ActivityLogsService activityLogsService;

  @Autowired
  public OrdersServiceImpl(CartRepository cartRepository, OrdersRepository ordersRepository,
      AccountRepository accountRepository, JwtGenerator jwtGenerator, MailService mailService,
      ActivityLogsService activityLogsService) {
    this.cartRepository = cartRepository;
    this.ordersRepository = ordersRepository;
    this.accountRepository = accountRepository;
    this.jwtGenerator = jwtGenerator;
    this.mailService = mailService;
    this.activityLogsService = activityLogsService;
  }


  @Override
  public List<OrdersDto> getAllOrders(String token) {
    validateIfAccountIsNotCustomerOrContentManager(token);
    logger.info("Getting all orders");

    List<Orders> orders = ordersRepository.findAll();
    orders = StreamEx.of(orders)
        .distinct(Orders::getOrderId)
        .toList();

    List<OrdersDto> dto = new ArrayList<>();

    for(Orders order: orders){
      if(order.getOrderType().equals(OrderType.ONLINE)){
        dto.add(new OrdersDto(order, getCartDtoListPerOrder(order)));
      }
    }

    dto.sort(Comparator.comparing(OrdersDto::getDateOrdered).reversed());
    return dto;
  }

  private List<CartDto> getCartDtoListPerOrder(Orders order){
    List<Cart> carts = cartRepository.findAllCartsByOrderId(order.getOrderId());
    List<CartDto> dto = new ArrayList<>();

    for(Cart cart: carts){
      dto.add(new CartDto(cart, order.getOrderStatus()));
    }

    return dto;
  }

  @Override
  public String verifyOrder(String token, String orderId) {
    validateIfAccountIsNotCustomerOrContentManager(token);

    Account account = getAccountFromToken(token);
    logger.info("verifying order {}", orderId);

    List<Orders> orders = ordersRepository.findByOrderId(orderId);
    Account customer = null;
    String refNo = "";
    OrderStatus status = null;

    for(Orders order: orders){
      validateOrderStatusForVerification(order);

      customer = order.getCart().getAccount();

      refNo = order.getReferenceNumber();
      status = order.getOrderStatus();

      order.setOrderStatus(OrderStatus.VERIFIED);
      ordersRepository.save(order);
    }

    if(customer == null){
      throw new ValidationException("Customer of the order does not have an online account");
    }

    mailService.sendEmailForVerified(customer, refNo);
    activityLogsService.onlineOrderActivity(account, refNo, status);

    return refNo;
  }

  private void validateOrderStatusForVerification(Orders orders){
    if(orders.getReferenceNumber().isEmpty() || orders.getReferenceNumber() == null){
      throw new ValidationException("Order cannot be verified due to missing/invalid reference number");
    }

    if(orders.getOrderStatus().equals(OrderStatus.VERIFIED)){
      throw new ValidationException("Order already verified");
    }

    if(orders.getOrderStatus().equals(OrderStatus.ORDER_COMPLETED)){
      throw new ValidationException("Order was already completed");
    }

    if(orders.getOrderStatus().equals(OrderStatus.FOR_DELIVERY)){
      throw new ValidationException("Cannot verify if status is already on delivery or invalid");
    }
  }

  @Override
  public String toBeDelivered(String token, String orderId) {
    validateIfAccountIsNotCustomerOrContentManager(token);
    logger.info("changing order {} status to 'for delivery'", orderId);

    Account account = getAccountFromToken(token);

    List<Orders> orders = ordersRepository.findByOrderId(orderId);
    Account customer = null;
    String refNo = "";
    OrderStatus status = null;

    for(Orders order: orders) {
      if (order.getOrderStatus() != OrderStatus.VERIFIED) {
        throw new ValidationException("Order not yet verified and cannot be delivered yet");
      }

      if (order.getOrderStatus().equals(OrderStatus.ORDER_COMPLETED)) {
        throw new ValidationException("Order was already completed");
      }

      customer = order.getCart().getAccount();

      refNo = order.getReferenceNumber();
      status = order.getOrderStatus();

      order.setOrderStatus(OrderStatus.FOR_DELIVERY);
      ordersRepository.save(order);
    }

    if(customer == null){
      throw new ValidationException("Customer of the order does not have an online account");
    }

    mailService.sendEmailForDelivery(customer, refNo);
    activityLogsService.onlineOrderActivity(account, refNo, status);

    return refNo;
  }

  @Override
  public String completeDelivery(String token, String orderId) {
    validateIfAccountIsNotCustomerOrContentManager(token);
    logger.info("completing order {}", orderId);

    Account account = getAccountFromToken(token);
    List<Orders> orders = ordersRepository.findByOrderId(orderId);
    Account customer = null;
    String refNo = "";
    OrderStatus status = null;

    for(Orders order: orders) {
      if (order.getOrderStatus().equals(OrderStatus.ORDER_COMPLETED)) {
        throw new ValidationException("Order already completed");
      }

      if (order.getOrderStatus() != OrderStatus.FOR_DELIVERY) {
        throw new ValidationException("Order must be delivered first before being completed");
      }

      customer = order.getCart().getAccount();

      refNo = order.getReferenceNumber();
      status = order.getOrderStatus();

      order.setOrderStatus(OrderStatus.ORDER_COMPLETED);
      ordersRepository.save(order);
    }

    if(customer == null){
      throw new ValidationException("Customer of the order does not have an online account");
    }

    mailService.sendEmailForOrderCompletion(customer);
    activityLogsService.onlineOrderActivity(account, refNo, status);

    return refNo;
  }

  @Override
  public String orderNotVerified(String token, String orderId) {
    validateIfAccountIsNotCustomerOrContentManager(token);
    logger.info("setting order {} to invalid order", orderId);

    Account account = getAccountFromToken(token);

    List<Orders> orders = ordersRepository.findByOrderId(orderId);
    Account customer = null;
    String refNo = "";
    OrderStatus status = null;

    for(Orders order: orders) {
      if (order.getOrderStatus().equals(OrderStatus.FOR_DELIVERY) || order.getOrderStatus()
          .equals(OrderStatus.ORDER_COMPLETED)) {
        throw new ValidationException(
            "Cannot change order status if already on delivery or completed");
      }

      customer = order.getCart().getAccount();

      refNo = order.getReferenceNumber();
      status = order.getOrderStatus();


      order.setOrderStatus(OrderStatus.INVALID_REFERENCE_NUMBER);
      ordersRepository.save(order);
    }

    if(customer == null){
      throw new ValidationException("Customer of the order does not have an online account");
    }

    mailService.sendEmailForInvalidPayment(customer, refNo);
    activityLogsService.onlineOrderActivity(account, refNo, status);

    return refNo;
  }

  private Account getAccountFromToken(String token){
    String id = jwtGenerator.getUserFromJWT(token);

    return accountRepository.findById(id).orElseThrow(() -> new NotFoundException("Account does not exists"));
  }

  private void validateIfAccountIsNotCustomerOrContentManager(String token){
    String accId = jwtGenerator.getUserFromJWT(token);
    Account adminAccount = accountRepository.findById(accId).orElseThrow(() -> new NotFoundException("Account does not exists"));

    if(!adminAccount.getUserRole().equals(UserRole.ADMINISTRATOR)){
      throw new ValidationException("Only Administrators, Product, and Order managers have access to this feature");
    }
  }
}
