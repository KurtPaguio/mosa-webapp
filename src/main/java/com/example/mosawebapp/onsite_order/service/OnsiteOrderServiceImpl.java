package com.example.mosawebapp.onsite_order.service;

import com.example.mosawebapp.account.domain.Account;
import com.example.mosawebapp.account.domain.AccountRepository;
import com.example.mosawebapp.account.domain.UserRole;
import com.example.mosawebapp.all_orders.domain.OrderType;
import com.example.mosawebapp.all_orders.domain.Orders;
import com.example.mosawebapp.all_orders.domain.OrdersRepository;
import com.example.mosawebapp.api_response.ApiObjectResponse;
import com.example.mosawebapp.api_response.ApiResponse;
import com.example.mosawebapp.exceptions.NotFoundException;
import com.example.mosawebapp.exceptions.ValidationException;
import com.example.mosawebapp.logs.service.ActivityLogsService;
import com.example.mosawebapp.onsite_order.domain.OnsiteOrder;
import com.example.mosawebapp.onsite_order.domain.OnsiteOrderCheckout;
import com.example.mosawebapp.onsite_order.domain.OnsiteOrderCheckoutRepository;
import com.example.mosawebapp.onsite_order.domain.OnsiteOrderItem;
import com.example.mosawebapp.onsite_order.domain.OnsiteOrderItemRepository;
import com.example.mosawebapp.onsite_order.domain.OnsiteOrderRepository;
import com.example.mosawebapp.onsite_order.dto.OnsiteOrderCheckoutDto;
import com.example.mosawebapp.onsite_order.dto.OnsiteOrderDto;
import com.example.mosawebapp.onsite_order.dto.OnsiteOrderItemDto;
import com.example.mosawebapp.onsite_order.dto.OrderForm;
import com.example.mosawebapp.product.brand.domain.Brand;
import com.example.mosawebapp.product.brand.domain.BrandRepository;
import com.example.mosawebapp.product.threadtype.domain.ThreadType;
import com.example.mosawebapp.product.threadtype.domain.ThreadTypeRepository;
import com.example.mosawebapp.product.threadtypedetails.domain.ThreadTypeDetails;
import com.example.mosawebapp.product.threadtypedetails.domain.ThreadTypeDetailsRepository;
import com.example.mosawebapp.security.JwtGenerator;
import com.example.mosawebapp.validate.Validate;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.internal.build.AllowPrintStacktrace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class OnsiteOrderServiceImpl implements OnsiteOrderService{
  private final OnsiteOrderRepository onsiteOrderRepository;
  private final OnsiteOrderItemRepository onsiteOrderItemRepository;
  private final OnsiteOrderCheckoutRepository onsiteOrderCheckoutRepository;
  private final OrdersRepository ordersRepository;
  private final AccountRepository accountRepository;
  private final JwtGenerator jwtGenerator;
  private final ThreadTypeRepository threadTypeRepository;
  private final ThreadTypeDetailsRepository threadTypeDetailsRepository;
  private final BrandRepository brandRepository;
  private final ActivityLogsService activityLogsService;

  @Autowired
  public OnsiteOrderServiceImpl(OnsiteOrderRepository onsiteOrderRepository,
      OnsiteOrderItemRepository onsiteOrderItemRepository,
      OnsiteOrderCheckoutRepository onsiteOrderCheckoutRepository,
      OrdersRepository ordersRepository, AccountRepository accountRepository, JwtGenerator jwtGenerator,
      ThreadTypeRepository threadTypeRepository,
      ThreadTypeDetailsRepository threadTypeDetailsRepository, BrandRepository brandRepository,
      ActivityLogsService activityLogsService) {
    this.onsiteOrderRepository = onsiteOrderRepository;
    this.onsiteOrderItemRepository = onsiteOrderItemRepository;
    this.onsiteOrderCheckoutRepository = onsiteOrderCheckoutRepository;
    this.ordersRepository = ordersRepository;
    this.accountRepository = accountRepository;
    this.jwtGenerator = jwtGenerator;
    this.threadTypeRepository = threadTypeRepository;
    this.threadTypeDetailsRepository = threadTypeDetailsRepository;
    this.brandRepository = brandRepository;
    this.activityLogsService = activityLogsService;
  }

  @Override
  public List<OnsiteOrderCheckoutDto> getAllOrders(String token) {
    validateIfAccountIsNotCustomerOrContentManager(token);
    List<OnsiteOrderCheckoutDto> dto = new ArrayList<>();

    List<OnsiteOrderCheckout> checkouts = onsiteOrderCheckoutRepository.findAll();

    for(OnsiteOrderCheckout checkout: checkouts){
      List<OnsiteOrderItem> items = onsiteOrderItemRepository.findByOrder(checkout.getOrder());
      dto.add(OnsiteOrderCheckoutDto.buildFromEntityV2(checkout, items));
    }

    return dto;
  }

  @Override
  public OnsiteOrderDto getOrder(String token, String orderId) {
    validateIfAccountIsNotCustomerOrContentManager(token);

    OnsiteOrder order = onsiteOrderRepository.findById(orderId).orElseThrow(() -> new NotFoundException("Order does not exists"));

    if(order == null){
      return null;
    }

    List<OnsiteOrderItem> items = onsiteOrderItemRepository.findByOrder(order);

    return new OnsiteOrderDto(order, items);
  }

  @Override
  public OnsiteOrderItemDto addOrderItem(String token, OrderForm form) {
    Validate.notNull(form);
    validateForm(form);
    validateIfAccountIsNotCustomerOrContentManager(token);

    OnsiteOrder order = onsiteOrderRepository.findLatestActiveOrder(true);
    ThreadType threadType = validateThreadType(form);
    ThreadTypeDetails details = validateThreadTypeDetails(form);

    if(order == null){
      order = onsiteOrderRepository.save(new OnsiteOrder(true));
    }

    OnsiteOrderItem existingItem = onsiteOrderItemRepository.isOrderFormAlreadyInCart(order, details.getThreadType(), details);
    if(existingItem != null){
      existingItem.setQuantity(existingItem.getQuantity() + form.getQuantity());
      onsiteOrderItemRepository.save(existingItem);

      return new OnsiteOrderItemDto(existingItem, details);
    }

    OnsiteOrderItem item = new OnsiteOrderItem(order, threadType, details, form.getQuantity());
    onsiteOrderItemRepository.save(item);

    return new OnsiteOrderItemDto(item, details);
  }

  @Override
  public void removeOrderItem(String token, String itemId) {
    validateIfAccountIsNotCustomerOrContentManager(token);
    OnsiteOrderItem item = onsiteOrderItemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Order item does not exists"));
    OnsiteOrder order = onsiteOrderRepository.findLatestActiveOrder(true);

    boolean isItemInCart = onsiteOrderItemRepository.isItemInCart(order,itemId);

    if(!isItemInCart){
      throw new ValidationException("Item not for the current cart");
    }

    onsiteOrderItemRepository.delete(item);
  }

  @Override
  public ResponseEntity<?> subtractOrderItemQuantity(String token, String itemId) {
    validateIfAccountIsNotCustomerOrContentManager(token);
    OnsiteOrderItem item = onsiteOrderItemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item does not exists"));

    OnsiteOrder order = onsiteOrderRepository.findLatestActiveOrder(true);
    ThreadTypeDetails details = threadTypeDetailsRepository.findById(item.getDetails().getId())
        .orElseThrow(() -> new NotFoundException("Thread Type Variant not found"));

    boolean isItemInCart = onsiteOrderItemRepository.isItemInCart(order,itemId);

    if(!isItemInCart){
      throw new ValidationException("Item not for the current cart");
    }

    item.setQuantity(item.getQuantity() - 1);
    onsiteOrderItemRepository.save(item);

    if(item.getQuantity() == 0){
      onsiteOrderItemRepository.delete(item);

      return ResponseEntity.ok(new ApiResponse("Item removed", HttpStatus.OK));
    }

    return ResponseEntity.ok(new ApiObjectResponse(HttpStatus.OK, "Item quantity subtracted", new OnsiteOrderItemDto(item, details)));
  }

  @Override
  public ResponseEntity<?> addOrderItemQuantity(String token, String itemId) {
    validateIfAccountIsNotCustomerOrContentManager(token);
    OnsiteOrderItem item = onsiteOrderItemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item does not exists"));

    OnsiteOrder order = onsiteOrderRepository.findLatestActiveOrder(true);
    ThreadTypeDetails details = threadTypeDetailsRepository.findById(item.getDetails().getId())
        .orElseThrow(() -> new NotFoundException("Thread Type Variant not found"));

    boolean isItemInCart = onsiteOrderItemRepository.isItemInCart(order,itemId);

    if(!isItemInCart){
      throw new ValidationException("Item not for the current cart");
    }

    item.setQuantity(item.getQuantity() + 1);
    onsiteOrderItemRepository.save(item);

    return ResponseEntity.ok(new ApiObjectResponse(HttpStatus.OK, "Item quantity added", new OnsiteOrderItemDto(item, details)));
  }

  @Override
  public void checkout(String token) {
    validateIfAccountIsNotCustomerOrContentManager(token);
    Account account = getAccountFromToken(token);

    OnsiteOrder order = onsiteOrderRepository.findLatestActiveOrder(true);

    if(order == null){
      throw new ValidationException("No latest order existing");
    }

    order.setActive(false);
    onsiteOrderRepository.save(order);

    OnsiteOrderCheckout checkout = new OnsiteOrderCheckout(order);

    onsiteOrderCheckoutRepository.save(checkout);

    OnsiteOrderItem item = onsiteOrderItemRepository.findItemByOrder(order);
    ThreadTypeDetails details = item.getDetails();
    details.setStocks(details.getStocks() - item.getQuantity());

    threadTypeDetailsRepository.save(details);
    activityLogsService.onsiteOrderCheckoutActivity(checkout, account, details);

    Orders orders = new Orders(order.getId(), OrderType.ONSITE);
    ordersRepository.save(orders);
  }

  private void validateForm(OrderForm form){
    Brand formBrand = brandRepository.findByNameIgnoreCase(form.getBrand());

    if(formBrand == null){
      brandRepository.findById(form.getBrand()).orElseThrow(() -> new NotFoundException("Brand does not exists"));
    }

    if(form.getQuantity() < 0){
      throw new ValidationException("Quantity must be greater than zero");
    }
  }

  private ThreadType validateThreadType(OrderForm form){
    ThreadType threadType = threadTypeRepository.findTypeId(form.getThreadType());

    if(threadType == null){
      threadType = threadTypeRepository.findByTypeIgnoreCase(form.getThreadType());

      if(threadType == null){
        throw new NotFoundException("Thread type does not exists");
      }

      validateBrandAndThreadTypeRelation(threadType, form);
    }

    validateBrandAndThreadTypeRelation(threadType, form);

    return threadType;
  }

  private ThreadTypeDetails validateThreadTypeDetails(OrderForm form){
    ThreadTypeDetails details = threadTypeDetailsRepository.findByDetails(form.getWidth().toLowerCase(), form.getAspectRatio().toLowerCase(), form.getDiameter().toLowerCase(),
        form.getSidewall().toLowerCase());

    if(details == null){
      throw new NotFoundException("No Thread Type Variant exists with these details");
    }

    if(!details.getThreadType().getType().equalsIgnoreCase(form.getThreadType()) && !details.getThreadType().getId().equals(form.getThreadType())){
      throw new ValidationException("Details not under the Thread Type " + form.getThreadType());
    }

    if(details.getStocks() == null || details.getStocks() == 0){
      throw new ValidationException("Already out of stock");
    }

    return details;
  }

  private void validateBrandAndThreadTypeRelation(ThreadType threadType, OrderForm form){
    if(!threadType.getBrand().getName().equalsIgnoreCase(form.getBrand()) && (!threadType.getBrand().getId().equals(form.getBrand()))){
      throw new ValidationException("Thread Type is not under the selected brand");
    }
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
