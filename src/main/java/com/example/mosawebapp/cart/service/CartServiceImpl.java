package com.example.mosawebapp.cart.service;

import com.example.mosawebapp.account.domain.Account;
import com.example.mosawebapp.account.domain.AccountRepository;
import com.example.mosawebapp.account.domain.UserRole;
import com.example.mosawebapp.all_orders.domain.OrderType;
import com.example.mosawebapp.all_orders.domain.Orders;
import com.example.mosawebapp.all_orders.domain.OrdersRepository;
import com.example.mosawebapp.api_response.ApiObjectResponse;
import com.example.mosawebapp.api_response.ApiResponse;
import com.example.mosawebapp.cart.domain.Cart;
import com.example.mosawebapp.cart.domain.CartCheckout;
import com.example.mosawebapp.cart.domain.CartCheckoutRepository;
import com.example.mosawebapp.cart.domain.CartItem;
import com.example.mosawebapp.cart.domain.CartItemRepository;
import com.example.mosawebapp.cart.domain.CartRepository;
import com.example.mosawebapp.cart.dto.CartCheckoutDto;
import com.example.mosawebapp.cart.dto.CartDto;
import com.example.mosawebapp.cart.dto.CartItemDto;
import com.example.mosawebapp.cart.dto.CartItemForm;
import com.example.mosawebapp.exceptions.NotFoundException;
import com.example.mosawebapp.exceptions.ValidationException;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CartServiceImpl implements CartService{
  private static final String CART_ITEM_NOT_EXIST = "Cart Item does not exists";
  private static final String CART_ITEM_NOT_FOR_USER = "Cart item does not belong in user's cart";
  private final CartRepository cartRepository;
  private final AccountRepository accountRepository;
  private final CartItemRepository cartItemRepository;
  private final ThreadTypeRepository threadTypeRepository;
  private final ThreadTypeDetailsRepository threadTypeDetailsRepository;
  private final OrdersRepository ordersRepository;
  private final CartCheckoutRepository cartCheckoutRepository;
  private final BrandRepository brandRepository;
  private final JwtGenerator jwtGenerator;

  @Autowired
  public CartServiceImpl(CartRepository cartRepository,
      AccountRepository accountRepository, CartItemRepository cartItemRepository,
      ThreadTypeRepository threadTypeRepository,
      ThreadTypeDetailsRepository threadTypeDetailsRepository,
      OrdersRepository ordersRepository, CartCheckoutRepository cartCheckoutRepository, BrandRepository brandRepository, JwtGenerator jwtGenerator) {
    this.cartRepository = cartRepository;
    this.accountRepository = accountRepository;
    this.cartItemRepository = cartItemRepository;
    this.threadTypeRepository = threadTypeRepository;
    this.threadTypeDetailsRepository = threadTypeDetailsRepository;
    this.ordersRepository = ordersRepository;
    this.cartCheckoutRepository = cartCheckoutRepository;
    this.brandRepository = brandRepository;
    this.jwtGenerator = jwtGenerator;
  }

  @Override
  public List<CartCheckoutDto> getCheckouts(String adminToken){
    Account account = getAccountFromToken(adminToken);

    if(account.getUserRole() == UserRole.CUSTOMER || account.getUserRole() == UserRole.CONTENT_MANAGER) {
      throw new ValidationException("Only Administrators, Product, and Order Managers can access this feature");
    }

    List<CartCheckoutDto> dto = new ArrayList<>();
    List<CartCheckout> checkouts = cartCheckoutRepository.findAll();

    for(CartCheckout checkout: checkouts){
      List<CartItem> items = cartItemRepository.findByCart(checkout.getCart());
      dto.add(CartCheckoutDto.buildFromEntityV2(checkout, items));
    }

    return dto;
  }

  @Override
  public List<CartDto> getAllCarts(){
    List<Cart> carts = cartRepository.findAll();

    List<CartDto> dtos = new ArrayList<>();
    for(Cart cart: carts){
      Account account = cart.getAccount();

      Cart userCart = cartRepository.findByAccount(account);

      List<CartItem> cartItems = cartItemRepository.findByCart(userCart);

      dtos.add(new CartDto(cart, account, cartItems));
    }

    return dtos;
  }

  @Override
  public CartDto getCart(String token) {
    Account account = getAccountFromToken(token);

    Cart cart = cartRepository.findByAccountAndIsActiveLatest(account, true);

    if(cart == null){
      return null;
    }

    List<CartItem> cartItems = cartItemRepository.findByCart(cart);

    return new CartDto(cart, account, cartItems);
  }

  @Override
  public void checkout(String token){
    Account account = getAccountFromToken(token);

    Cart cart = cartRepository.findByAccountAndIsActiveLatest(account, true);

    if(cart == null){
      throw new ValidationException("No latest cart existing for this user");
    }

    cart.setActive(false);
    cartRepository.save(cart);

    CartCheckout checkout = new CartCheckout(cart, account);

    cartCheckoutRepository.save(checkout);

    CartItem item = cartItemRepository.findItemByCart(cart);
    ThreadTypeDetails details = item.getDetails();
    details.setStocks(details.getStocks() - item.getQuantity());

    threadTypeDetailsRepository.save(details);

    Orders orders = new Orders(cart.getId(), OrderType.ONLINE);
    ordersRepository.save(orders);
  }

  @Override
  public CartItemDto addCartItem(String token, CartItemForm form) {
    Validate.notNull(form);
    validateForm(form);
    Account account = getAccountFromToken(token);
    Cart cart = cartRepository.findByAccountAndIsActiveLatest(account, true);
    ThreadType threadType = validateThreadType(form);
    ThreadTypeDetails details = validateThreadTypeDetails(form);

    if(cart == null){
      cart = cartRepository.save(new Cart(account, true));
    }

    CartItem existingItem = cartItemRepository.isCartFormAlreadyInUserCart(cart, details.getThreadType(), details);
    if(existingItem != null){
      existingItem.setQuantity(existingItem.getQuantity() + form.getQuantity());
      cartItemRepository.save(existingItem);

      return new CartItemDto(existingItem, details);
    }

    CartItem item = new CartItem(cart, threadType, details, form.getQuantity());
    cartItemRepository.save(item);

    return new CartItemDto(item, details);
  }

  @Override
  public void removeCartItem(String token, String itemId) {
    Account account = getAccountFromToken(token);
    CartItem cartItem = cartItemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(CART_ITEM_NOT_EXIST));
    Cart cart = cartRepository.findByAccountAndIsActiveLatest(account, true);

    boolean isCartItemInUserCart = cartItemRepository.isCartItemInUserCart(cart, itemId);

    if(!isCartItemInUserCart){
      throw new ValidationException(CART_ITEM_NOT_FOR_USER);
    }

    cartItemRepository.delete(cartItem);
  }

  @Override
  public ResponseEntity<?> subtractCartItemQuantity(String token, String itemId) {
    Account account = getAccountFromToken(token);
    CartItem cartItem = cartItemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(CART_ITEM_NOT_EXIST));

    Cart cart = cartRepository.findByAccountAndIsActiveLatest(account, true);
    ThreadTypeDetails details = threadTypeDetailsRepository.findById(cartItem.getDetails().getId())
        .orElseThrow(() -> new NotFoundException("Thread Type Variant not found"));

    boolean isCartItemInUserCart = cartItemRepository.isCartItemInUserCart(cart, itemId);

    if(!isCartItemInUserCart){
      throw new ValidationException(CART_ITEM_NOT_FOR_USER);
    }

    cartItem.setQuantity(cartItem.getQuantity() - 1);
    cartItemRepository.save(cartItem);

    if(cartItem.getQuantity() == 0){
      cartItemRepository.delete(cartItem);

      return ResponseEntity.ok(new ApiResponse("Item removed from cart", HttpStatus.OK));
    }

    return ResponseEntity.ok(new ApiObjectResponse(HttpStatus.OK, "Item quantity subtracted", new CartItemDto(cartItem, details)));
  }

  @Override
  public ResponseEntity<?> addCartItemQuantity(String token, String itemId) {
    Account account = getAccountFromToken(token);
    CartItem cartItem = cartItemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(CART_ITEM_NOT_EXIST));

    Cart cart = cartRepository.findByAccountAndIsActiveLatest(account, true);
    ThreadTypeDetails details = threadTypeDetailsRepository.findById(cartItem.getDetails().getId())
        .orElseThrow(() -> new NotFoundException("Thread Type Variant not found"));

    boolean isCartItemInUserCart = cartItemRepository.isCartItemInUserCart(cart, itemId);

    if(!isCartItemInUserCart){
      throw new ValidationException(CART_ITEM_NOT_FOR_USER);
    }

    cartItem.setQuantity(cartItem.getQuantity() + 1);
    cartItemRepository.save(cartItem);

    return ResponseEntity.ok(new ApiObjectResponse(HttpStatus.OK, "Item quantity added", new CartItemDto(cartItem, details)));
  }

  private Account getAccountFromToken(String token){
    String id = jwtGenerator.getUserFromJWT(token);

    return accountRepository.findById(id).orElseThrow(() -> new NotFoundException("Account does not exists"));
  }

  private void validateForm(CartItemForm form){
    Brand formBrand = brandRepository.findByNameIgnoreCase(form.getBrand());

    if(formBrand == null){
      brandRepository.findById(form.getBrand()).orElseThrow(() -> new NotFoundException("Brand does not exists"));
    }

    if(form.getQuantity() < 0){
      throw new ValidationException("Quantity must be greater than zero");
    }
  }

  private ThreadType validateThreadType(CartItemForm form){
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

  private ThreadTypeDetails validateThreadTypeDetails(CartItemForm form){
    ThreadTypeDetails details = threadTypeDetailsRepository.findByDetails(form.getWidth().toLowerCase(), form.getAspectRatio().toLowerCase(), form.getDiameter().toLowerCase(),
        form.getSidewall().toLowerCase(), form.getPlyRating().toLowerCase());

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

  private void validateBrandAndThreadTypeRelation(ThreadType threadType, CartItemForm form){
    if(!threadType.getBrand().getName().equalsIgnoreCase(form.getBrand()) && (!threadType.getBrand().getId().equals(form.getBrand()))){
        throw new ValidationException("Thread Type is not under the selected brand");
    }
  }
}
