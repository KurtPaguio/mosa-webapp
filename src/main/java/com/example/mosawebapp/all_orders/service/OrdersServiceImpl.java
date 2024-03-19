package com.example.mosawebapp.all_orders.service;

import com.example.mosawebapp.account.domain.Account;
import com.example.mosawebapp.account.domain.AccountRepository;
import com.example.mosawebapp.account.domain.UserRole;
import com.example.mosawebapp.account.dto.AccountDto;
import com.example.mosawebapp.all_orders.domain.Orders;
import com.example.mosawebapp.all_orders.domain.OrdersRepository;
import com.example.mosawebapp.all_orders.dto.OrdersDto;
import com.example.mosawebapp.cart.domain.Cart;
import com.example.mosawebapp.cart.domain.CartRepository;
import com.example.mosawebapp.exceptions.NotFoundException;
import com.example.mosawebapp.exceptions.ValidationException;
import com.example.mosawebapp.security.JwtGenerator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OrdersServiceImpl implements OrdersService{
  private static final Logger logger = LoggerFactory.getLogger(OrdersServiceImpl.class);
  private final CartRepository cartRepository;
  private final OrdersRepository ordersRepository;
  private final AccountRepository accountRepository;
  private final JwtGenerator jwtGenerator;

  public OrdersServiceImpl(CartRepository cartRepository, OrdersRepository ordersRepository,
      AccountRepository accountRepository, JwtGenerator jwtGenerator) {
    this.cartRepository = cartRepository;
    this.ordersRepository = ordersRepository;
    this.accountRepository = accountRepository;
    this.jwtGenerator = jwtGenerator;
  }


  @Override
  public List<OrdersDto> getAllOrders(String token) {
    validateIfAccountIsNotCustomerOrContentManager(token);

    logger.info("getting all orders");

    List<Orders> orders = ordersRepository.findAll();
    List<OrdersDto> dto = new ArrayList<>();

    for(Orders order: orders){
      cartRepository.findById(order.getOrderId()).ifPresent(cart -> dto.add(new OrdersDto(order, cart)));
    }

    dto.sort(Comparator.comparing(OrdersDto::getDateOrdered).reversed());
    return dto;
  }

  private void validateIfAccountIsNotCustomerOrContentManager(String token){
    String accId = jwtGenerator.getUserFromJWT(token);
    Account adminAccount = accountRepository.findById(accId).orElseThrow(() -> new NotFoundException("Account does not exists"));

    if(!adminAccount.getUserRole().equals(UserRole.ADMINISTRATOR)){
      throw new ValidationException("Only Administrators, Product, and Order managers have access to this feature");
    }
  }
}
