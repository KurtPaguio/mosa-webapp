package com.example.mosawebapp.onsite_order.service;

import com.example.mosawebapp.account.domain.Account;
import com.example.mosawebapp.account.domain.AccountRepository;
import com.example.mosawebapp.account.domain.UserRole;
import com.example.mosawebapp.all_orders.domain.OrderStatus;
import com.example.mosawebapp.all_orders.domain.OrderType;
import com.example.mosawebapp.all_orders.domain.Orders;
import com.example.mosawebapp.all_orders.domain.OrdersRepository;
import com.example.mosawebapp.cart.domain.Cart;
import com.example.mosawebapp.cart.dto.CartCheckoutDto;
import com.example.mosawebapp.cart.dto.CartDto;
import com.example.mosawebapp.cart.dto.CheckoutForm;
import com.example.mosawebapp.cart.dto.OrderForm;
import com.example.mosawebapp.cart.dto.ReferenceNumberForm;
import com.example.mosawebapp.exceptions.NotFoundException;
import com.example.mosawebapp.exceptions.ValidationException;
import com.example.mosawebapp.logs.service.ActivityLogsService;
import com.example.mosawebapp.onsite_order.domain.OnsiteOrder;
import com.example.mosawebapp.onsite_order.domain.OnsiteOrderRepository;
import com.example.mosawebapp.onsite_order.dto.OnsiteOrderCheckoutDto;
import com.example.mosawebapp.onsite_order.dto.OnsiteOrderDto;
import com.example.mosawebapp.product.threadtype.domain.ThreadType;
import com.example.mosawebapp.product.threadtype.domain.ThreadTypeRepository;
import com.example.mosawebapp.product.threadtypedetails.domain.ThreadTypeDetails;
import com.example.mosawebapp.product.threadtypedetails.domain.ThreadTypeDetailsRepository;
import com.example.mosawebapp.security.JwtGenerator;
import com.example.mosawebapp.validate.Validate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OnsiteOrderServiceImpl implements OnsiteOrderService {
  private static final Logger logger = LoggerFactory.getLogger(OnsiteOrderServiceImpl.class);

  private final AccountRepository accountRepository;
  private final JwtGenerator jwtGenerator;
  private final ThreadTypeRepository threadTypeRepository;
  private final ThreadTypeDetailsRepository threadTypeDetailsRepository;
  private final OrdersRepository ordersRepository;
  private final OnsiteOrderRepository onsiteOrderRepository;
  private final ActivityLogsService activityLogsService;

  @Autowired
  public OnsiteOrderServiceImpl(AccountRepository accountRepository, JwtGenerator jwtGenerator,
      ThreadTypeRepository threadTypeRepository,
      ThreadTypeDetailsRepository threadTypeDetailsRepository, OrdersRepository ordersRepository,
      OnsiteOrderRepository onsiteOrderRepository, ActivityLogsService activityLogsService) {
    this.accountRepository = accountRepository;
    this.jwtGenerator = jwtGenerator;
    this.threadTypeRepository = threadTypeRepository;
    this.threadTypeDetailsRepository = threadTypeDetailsRepository;
    this.ordersRepository = ordersRepository;
    this.onsiteOrderRepository = onsiteOrderRepository;
    this.activityLogsService = activityLogsService;
  }

  @Override
  public void startOrder(String token) {
    logger.info("generating token for onsite ordering");

    validateIfAccountIsNotCustomerOrContentManager(token);

    Account account = getAccountFromToken(token);
    account.setOrdering(true);
    accountRepository.save(account);
  }

  @Override
  public List<OnsiteOrderDto> getAllOrders(String token) {
    logger.info("getting all orders");
    validateIfAccountIsNotCustomerOrContentManager(token);

    List<OnsiteOrder> orders = onsiteOrderRepository.findAll();
    List<OnsiteOrderDto> dto = new ArrayList<>();

    for(OnsiteOrder order: orders){
      dto.add(new OnsiteOrderDto(order));
    }

    dto.sort(Comparator.comparing(OnsiteOrderDto::getDateCreated).reversed());
    return dto;
  }

  @Override
  public List<OnsiteOrderDto> getAllCurrentOrders(String token) {
    logger.info("getting current orders");

    List<OnsiteOrder> orders = onsiteOrderRepository.findByIsBeingOrderedStatusAsTrue();

    if(orders.isEmpty()){
      return null;
    }

    List<OnsiteOrderDto> dto = new ArrayList<>();

    for(OnsiteOrder order: orders){
      dto.add(new OnsiteOrderDto(order));
    }

    dto.sort(Comparator.comparing(OnsiteOrderDto::getDateCreated).reversed());
    return dto;
  }

  @Override
  public OnsiteOrderDto getOnsiteOrder(String token, String orderId) {
    return null; // Not relevant for now
  }

  @Override
  public OnsiteOrderDto addOrder(String token, OrderForm form) {
    validateIfAccountIsNotCustomerOrContentManager(token);
    Account account = getAccountFromToken(token);

    Validate.notNull(form);

    ThreadType type = validateThreadType(form.getThreadType());
    ThreadTypeDetails details = validateThreadTypeDetails(form, type);

    validateQuantityAndStocks(form, details);

    logger.info("saving onsite orders");

    OnsiteOrder order = onsiteOrderRepository.findByIsBeingOrderedStatusAndTypeAndDetailsAndNotCheckedOut(type, details);
    if(order != null){
      order.setQuantity(order.getQuantity() + form.getQuantity());
      onsiteOrderRepository.save(order);

      return new OnsiteOrderDto(order);
    }

    OnsiteOrder onsiteOrder = new OnsiteOrder(type, details, form.getQuantity(), (form.getQuantity() * details.getPrice()), false, true);
    onsiteOrderRepository.save(onsiteOrder);

    activityLogsService.onsiteOrderActivity(account, "added", onsiteOrder);
    return new OnsiteOrderDto(onsiteOrder);
  }

  private ThreadType validateThreadType(String threadType){
    ThreadType type = threadTypeRepository.findByTypeIgnoreCase(threadType);

    if(type == null){
      threadTypeRepository.findById(threadType).orElseThrow(() -> new NotFoundException("Thread Type does not exists"));
    }

    return type;
  }

  private ThreadTypeDetails validateThreadTypeDetails(OrderForm form, ThreadType type){
    logger.info("validating thread type details");
    ThreadTypeDetails details = threadTypeDetailsRepository.findByDetails(type.getId(),
        form.getWidth(), form.getAspectRatio(), form.getDiameter(), form.getSidewall());

    if(details == null){
      throw new NotFoundException("Thread Type with these details does not exists");
    }

    if(!(form.getThreadType().equals(details.getThreadType().getId())
        || form.getThreadType().equalsIgnoreCase(details.getThreadType().getType()))){
      throw new NotFoundException("Details under this Thread Type does not exists");
    }

    return details;
  }

  private void validateQuantityAndStocks(OrderForm form, ThreadTypeDetails details){
    if(details.getStocks() == 0){
      throw new ValidationException("Entered thread type and its variant is out of stock");
    }

    if(form.getQuantity() < 1){
      throw new ValidationException("Quantity must be greater than or equal to one");
    }

    if(form.getQuantity() > details.getStocks()){
      throw new ValidationException("Current stock is lower than the entered quantity by " + (form.getQuantity() - details.getStocks()));
    }
  }

  @Override
  public OnsiteOrderDto addOrderQuantity(String token, String orderId) {
    validateIfAccountIsNotCustomerOrContentManager(token);

    OnsiteOrder order = onsiteOrderRepository.findOrderById(orderId);

    if(order == null){
      throw new NotFoundException("Order does not exists");
    }

    if(!order.isBeingOrdered() || order.isPaid()){
      throw new ValidationException("Cannot edit quantity of an order not being ordered or already paid");
    }

    logger.info("adding order quantity of order {}", orderId);

    order.setQuantity(order.getQuantity() + 1);
    onsiteOrderRepository.save(order);

    return new OnsiteOrderDto(order);
  }

  @Override
  public void removeOrder(String token, String orderId) {
    validateIfAccountIsNotCustomerOrContentManager(token);
    Account account = getAccountFromToken(token);

    OnsiteOrder order = onsiteOrderRepository.findOrderById(orderId);

    if(order == null){
      throw new NotFoundException("Order does not exists");
    }

    if(!order.isBeingOrdered() || order.isPaid()){
      throw new ValidationException("Cannot remove completed order");
    }

    logger.info("deleting order {}", orderId);

    activityLogsService.onsiteOrderActivity(account, "deleted", order);
    onsiteOrderRepository.delete(order);
  }

  @Override
  public OnsiteOrderDto subtractOrderQuantity(String token, String orderId) {
    validateIfAccountIsNotCustomerOrContentManager(token);

    OnsiteOrder order = onsiteOrderRepository.findOrderById(orderId);

    if(order == null){
      throw new NotFoundException("Order does not exists");
    }

    if(!order.isBeingOrdered() || order.isPaid()){
      throw new ValidationException("Cannot edit quantity of an order not being ordered or already paid");
    }

    logger.info("subtracting order quantity of order {}", orderId);

    order.setQuantity(order.getQuantity() - 1);
    onsiteOrderRepository.save(order);

    return new OnsiteOrderDto(order);
  }

  @Override
  public OnsiteOrderCheckoutDto checkout(String token, CheckoutForm form) {
    Validate.notNull(form);
    validateIfAccountIsNotCustomerOrContentManager(token);
    Account account = getAccountFromToken(token);

    List<String> orderIds = new ArrayList<>(form.getIds());

    orderIds.removeIf(id -> {
      OnsiteOrder order = onsiteOrderRepository.findOrderById(id);
      return order != null && order.isPaid() && !order.isBeingOrdered();
    });

    logger.info("checking out selected orders");

    List<OnsiteOrderDto> dto = new ArrayList<>();

    String orderId = UUID.randomUUID().toString();
    for(String id: orderIds){
      OnsiteOrder order = onsiteOrderRepository.findOrderById(id);

      if(order == null){
        throw new NotFoundException("Order does not exists");
      }

      if(!order.isBeingOrdered() || order.isPaid()){
        throw new ValidationException("Cannot checkout an order not being ordered or already paid");
      }

      order.setPaid(true);
      order.setBeingOrdered(false);
      onsiteOrderRepository.save(order);
      dto.add(new OnsiteOrderDto(order));

      Orders orders = new Orders(OrderType.ONSITE, OrderStatus.ORDER_COMPLETED, null, null, null, null, order, orderId);
      ordersRepository.save(orders);

      ThreadTypeDetails details = order.getDetails();
      details.setStocks(details.getStocks() - order.getQuantity());
      threadTypeDetailsRepository.save(details);
    }

    activityLogsService.onsiteOrderCheckout(account, dto);
    return new OnsiteOrderCheckoutDto(dto);
  }

  @Override
  public void cancelCheckout(String token, CheckoutForm form) {
    Validate.notNull(form);

    List<String> orderIds = new ArrayList<>(form.getIds());

    logger.info("cancelling check out of orders");

    for(String id: orderIds){
      OnsiteOrder order = onsiteOrderRepository.findOrderById(id);

      if(order == null){
        throw new NotFoundException("Order does not exists");
      }

      if(order.isPaid()){
        throw new ValidationException("Cannot cancel an order that is already paid");
      }

      onsiteOrderRepository.delete(order);
    }

    logger.info("Successfully cancelled the checkouts");
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
