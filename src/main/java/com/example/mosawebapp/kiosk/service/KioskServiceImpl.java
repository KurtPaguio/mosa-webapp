package com.example.mosawebapp.kiosk.service;

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
import com.example.mosawebapp.kiosk.domain.Kiosk;
import com.example.mosawebapp.kiosk.domain.KioskCheckout;
import com.example.mosawebapp.kiosk.domain.KioskCheckoutRepository;
import com.example.mosawebapp.kiosk.domain.KioskOrder;
import com.example.mosawebapp.kiosk.domain.KioskOrderRepository;
import com.example.mosawebapp.kiosk.domain.KioskRepository;
import com.example.mosawebapp.kiosk.dto.KioskCheckoutDto;
import com.example.mosawebapp.kiosk.dto.KioskDto;
import com.example.mosawebapp.kiosk.dto.KioskOrderDto;
import com.example.mosawebapp.kiosk.dto.KioskOrderForm;
import com.example.mosawebapp.kiosk.dto.KioskOrderQuantityForm;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class KioskServiceImpl implements KioskService{
  private static final Logger logger = LoggerFactory.getLogger(KioskServiceImpl.class);
  private static final String ORDER_NOT_IN_CART = "Kiosk Order Item not in current cart";
  private final KioskOrderRepository kioskOrderRepository;
  private final KioskRepository kioskRepository;
  private final KioskCheckoutRepository kioskCheckoutRepository;
  private final OrdersRepository ordersRepository;
  private final JwtGenerator jwtGenerator;
  private final AccountRepository accountRepository;
  private final ThreadTypeRepository threadTypeRepository;
  private final ThreadTypeDetailsRepository threadTypeDetailsRepository;
  private final BrandRepository brandRepository;

  @Autowired
  public KioskServiceImpl(KioskOrderRepository kioskOrderRepository,
      KioskRepository kioskRepository, KioskCheckoutRepository kioskCheckoutRepository,
      OrdersRepository ordersRepository, JwtGenerator jwtGenerator,
      AccountRepository accountRepository, ThreadTypeRepository threadTypeRepository,
      ThreadTypeDetailsRepository threadTypeDetailsRepository, BrandRepository brandRepository) {
    this.kioskOrderRepository = kioskOrderRepository;
    this.kioskRepository = kioskRepository;
    this.kioskCheckoutRepository = kioskCheckoutRepository;
    this.ordersRepository = ordersRepository;
    this.jwtGenerator = jwtGenerator;
    this.accountRepository = accountRepository;
    this.threadTypeRepository = threadTypeRepository;
    this.threadTypeDetailsRepository = threadTypeDetailsRepository;
    this.brandRepository = brandRepository;
  }

  @Override
  public KioskDto startOrdering() {
    Kiosk kiosk = new Kiosk(true);
    kioskRepository.save(kiosk);

    logger.info("created kiosk for ordering with number {}", kiosk.getKioskNumber());
    return KioskDto.buildFromEntity(kiosk);
  }

  @Override
  public List<KioskCheckoutDto> getCheckouts(String adminToken) {
    String adminId = jwtGenerator.getUserFromJWT(adminToken);
    Account account = accountRepository.findById(adminId).orElseThrow(() -> new NotFoundException("Administrator account does not exists"));

    if(account.getUserRole() == UserRole.CUSTOMER || account.getUserRole() == UserRole.CONTENT_MANAGER){
      throw new ValidationException("Only Administrators, Product, and Order Managers can access this feature");
    }

    List<KioskCheckoutDto> dto = new ArrayList<>();
    List<KioskCheckout> checkouts = kioskCheckoutRepository.findAll();

    for(KioskCheckout checkout: checkouts){
      List<KioskOrder> orders = kioskOrderRepository.findByKiosk(checkout.getKiosk());
      dto.add(KioskCheckoutDto.buildFromEntityV2(checkout, orders));
    }

    return dto;
  }

  @Override
  public KioskDto getKiosk(String kioskNumber) {
    Kiosk kiosk = kioskRepository.findByKioskNumber(kioskNumber);

    if(kiosk == null){
      throw new ValidationException("Kiosk with number " + kioskNumber + " does not exists");
    }

    List<KioskOrder> orders = kioskOrderRepository.findByKiosk(kiosk);

    if(orders.isEmpty()){
      return KioskDto.buildFromEntity(kiosk);
    }

    return new KioskDto(kiosk, orders);
  }

  @Override
  public KioskOrderDto addKioskOrder(String kioskNumber, KioskOrderForm form) {
    Validate.notNull(form);
    validateForm(form);

    logger.info("KIOSK NUMBER: {}", kioskNumber);
    Kiosk kiosk = kioskRepository.findByKioskNumberAndIsActiveLatest(kioskNumber, true);
    ThreadType threadType = validateThreadType(form);
    ThreadTypeDetails details = validateThreadTypeDetails(form);

    if(kiosk == null){
      kiosk = kioskRepository.save(new Kiosk(true));
    }

    KioskOrder existingOrder = kioskOrderRepository.isKioskOrderFormAlreadyInCurrentCart(kiosk, details.getThreadType(), details);

    if(existingOrder != null){
      existingOrder.setQuantity(existingOrder.getQuantity() + form.getQuantity());
      kioskOrderRepository.save(existingOrder);

      return new KioskOrderDto(existingOrder, details);
    }

    KioskOrder order = new KioskOrder(kiosk, threadType, details, form.getQuantity());
    kioskOrderRepository.save(order);

    return new KioskOrderDto(order, details);
  }

  @Override
  public void removeKioskOrder(KioskOrderQuantityForm form) {
    KioskOrder order = kioskOrderRepository.findById(form.getItemId()).orElseThrow(() -> new NotFoundException("Kiosk Order does not exists"));
    Kiosk kiosk = kioskRepository.findByKioskNumberAndIsActiveLatest(form.getKioskNumber(), true);

    boolean isKioskOrderItemInCurrentCart = kioskOrderRepository.isKioskOrderInCurrentCart(kiosk, form.getItemId());

    if(!isKioskOrderItemInCurrentCart){
      throw new ValidationException(ORDER_NOT_IN_CART);
    }

    kioskOrderRepository.delete(order);
  }

  @Override
  public ResponseEntity<?> subtractKioskOrderQuantity(KioskOrderQuantityForm form) {
    KioskOrder order = kioskOrderRepository.findById(form.getItemId()).orElseThrow(() -> new NotFoundException("Kiosk Order does not exists"));

    Kiosk kiosk = kioskRepository.findByKioskNumberAndIsActiveLatest(form.getKioskNumber(), true);
    ThreadTypeDetails details = threadTypeDetailsRepository.findById(order.getDetails().getId())
        .orElseThrow(() -> new NotFoundException("Thread Type Variant not found"));

    boolean isKioskOrderItemInCurrentCart = kioskOrderRepository.isKioskOrderInCurrentCart(kiosk, form.getItemId());

    if(!isKioskOrderItemInCurrentCart){
      throw new ValidationException(ORDER_NOT_IN_CART);
    }

    order.setQuantity(order.getQuantity() - 1);
    kioskOrderRepository.save(order);

    if(order.getQuantity() == 0){
      kioskOrderRepository.delete(order);

      return ResponseEntity.ok(new ApiResponse("Item removed from order", HttpStatus.OK));
    }

    return ResponseEntity.ok(new ApiObjectResponse(HttpStatus.OK, "Item quantity subtracted", new KioskOrderDto(order, details)));
  }

  @Override
  public ResponseEntity<?> addKioskOrderQuantity(KioskOrderQuantityForm form) {
    KioskOrder order = kioskOrderRepository.findById(form.getItemId()).orElseThrow(() -> new NotFoundException("Kiosk Order does not exists"));

    Kiosk kiosk = kioskRepository.findByKioskNumberAndIsActiveLatest(form.getKioskNumber(), true);
    ThreadTypeDetails details = threadTypeDetailsRepository.findById(order.getDetails().getId())
        .orElseThrow(() -> new NotFoundException("Thread Type Variant not found"));

    boolean isKioskOrderItemInCurrentCart = kioskOrderRepository.isKioskOrderInCurrentCart(kiosk, form.getItemId());

    if(!isKioskOrderItemInCurrentCart){
      throw new ValidationException(ORDER_NOT_IN_CART);
    }

    order.setQuantity(order.getQuantity() + 1);
    kioskOrderRepository.save(order);

    return ResponseEntity.ok(new ApiObjectResponse(HttpStatus.OK, "Item quantity added", new KioskOrderDto(order, details)));
  }

  @Override
  public void checkout(String kioskNumber) {
    Kiosk kiosk = kioskRepository.findByKioskNumberAndIsActiveLatest(kioskNumber, true);

    if(kiosk == null){
      throw new ValidationException("No latest kiosk order existing");
    }

    kiosk.setActive(false);
    kioskRepository.save(kiosk);

    KioskCheckout checkout = new KioskCheckout(kiosk);

    kioskCheckoutRepository.save(checkout);

    KioskOrder order = kioskOrderRepository.findOrderByKiosk(kiosk);
    ThreadTypeDetails details = order.getDetails();
    details.setStocks(details.getStocks() - order.getQuantity());

    threadTypeDetailsRepository.save(details);

    Orders orders = new Orders(kiosk.getId(), OrderType.KIOSK);
    ordersRepository.save(orders);
  }

  private void validateForm(KioskOrderForm form){
    Brand formBrand = brandRepository.findByNameIgnoreCase(form.getBrand());

    if(formBrand == null){
      brandRepository.findById(form.getBrand()).orElseThrow(() -> new NotFoundException("Brand does not exists"));
    }

    if(form.getQuantity() < 0){
      throw new ValidationException("Quantity must be greater than zero");
    }
  }

  private ThreadType validateThreadType(KioskOrderForm form){
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

  private ThreadTypeDetails validateThreadTypeDetails(KioskOrderForm form){
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

  private void validateBrandAndThreadTypeRelation(ThreadType threadType, KioskOrderForm form){
    if(!threadType.getBrand().getName().equalsIgnoreCase(form.getBrand()) && (!threadType.getBrand().getId().equals(form.getBrand()))){
      throw new ValidationException("Thread Type is not under the selected brand");
    }
  }
}
