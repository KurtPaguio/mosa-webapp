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
import com.example.mosawebapp.exceptions.NotFoundException;
import com.example.mosawebapp.exceptions.ValidationException;
import com.example.mosawebapp.mail.MailService;
import com.example.mosawebapp.security.JwtGenerator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  public OrdersServiceImpl(CartRepository cartRepository, OrdersRepository ordersRepository,
      AccountRepository accountRepository, JwtGenerator jwtGenerator, MailService mailService) {
    this.cartRepository = cartRepository;
    this.ordersRepository = ordersRepository;
    this.accountRepository = accountRepository;
    this.jwtGenerator = jwtGenerator;
    this.mailService = mailService;
  }


  @Override
  public List<OrdersDto> getAllOrders(String token) {
    validateIfAccountIsNotCustomerOrContentManager(token);

    logger.info("getting all orders");

    List<Orders> orders = ordersRepository.findAll();
    List<OrdersDto> dto = new ArrayList<>();

    for(Orders order: orders) {
      List<String> orderIds = Arrays.asList(order.getOrderIds().split(","));

      if(order.getOrderType().equals(OrderType.ONLINE)) {
        for (String id : orderIds) {
          Cart cart = cartRepository.findById(id).orElse(null);

          dto.add(new OrdersDto(order, cart));
        }
      }
    }
    dto.sort(Comparator.comparing(OrdersDto::getDateOrdered).reversed());
    return dto;
  }

  @Override
  public OrdersDto verifyPayment(String token, String orderId) {
    validateIfAccountIsNotCustomerOrContentManager(token);
    Orders orders = ordersRepository.findById(orderId).orElseThrow(() -> new NotFoundException(ORDER_NOT_EXISTS));

    if(orders.getReferenceNumber().isEmpty() || orders.getReferenceNumber() == null){
      throw new ValidationException("Order cannot be verified due to missing/invalid reference number");
    }

    Cart cart = findCartByOrderIds(orders);

    orders.setOrderStatus(OrderStatus.VERIFIED);
    ordersRepository.save(orders);

    mailService.sendEmailForVerified(cart.getAccount(), orders);
    return new OrdersDto(orders);
  }

  @Override
  public OrdersDto toBeDelivered(String token, String orderId) {
    validateIfAccountIsNotCustomerOrContentManager(token);
    Orders orders = ordersRepository.findById(orderId).orElseThrow(() -> new NotFoundException(ORDER_NOT_EXISTS));

    if(orders.getOrderStatus() != OrderStatus.VERIFIED){
      throw new ValidationException("Order not yet verified and cannot be delivered yet");
    }

    //Cart cart = findCartByOrderIds(orders);

    orders.setOrderStatus(OrderStatus.FOR_DELIVERY);
    ordersRepository.save(orders);

    //mailService.sendEmailForToBeDelivered()
    return new OrdersDto(orders);
  }

  @Override
  public OrdersDto paymentNotVerified(String token, String orderId) {
    validateIfAccountIsNotCustomerOrContentManager(token);
    Orders orders = ordersRepository.findById(orderId).orElseThrow(() -> new NotFoundException(ORDER_NOT_EXISTS));

    //Cart cart = findCartByOrderIds(orders);

    orders.setOrderStatus(OrderStatus.INVALID_REFERENCE_NUMBER);
    ordersRepository.save(orders);

    //mailService.sendEmailForNotVerified()
    return new OrdersDto(orders);
  }

  private Cart findCartByOrderIds(Orders orders){
    String[] orderIds = orders.getOrderIds().split(",");

    return cartRepository.findById(orderIds[0]).orElseThrow(() -> new NotFoundException("Card does not exists with order id " + orders.getId()));
  }
  private void validateIfAccountIsNotCustomerOrContentManager(String token){
    String accId = jwtGenerator.getUserFromJWT(token);
    Account adminAccount = accountRepository.findById(accId).orElseThrow(() -> new NotFoundException("Account does not exists"));

    if(!adminAccount.getUserRole().equals(UserRole.ADMINISTRATOR)){
      throw new ValidationException("Only Administrators, Product, and Order managers have access to this feature");
    }
  }
}
