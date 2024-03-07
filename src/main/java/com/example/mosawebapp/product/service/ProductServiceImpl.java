package com.example.mosawebapp.product.service;

import com.example.mosawebapp.account.domain.Account;
import com.example.mosawebapp.account.domain.AccountRepository;
import com.example.mosawebapp.account.service.AccountService;
import com.example.mosawebapp.exceptions.NotFoundException;
import com.example.mosawebapp.logs.service.ActivityLogsService;
import com.example.mosawebapp.mail.MailService;
import com.example.mosawebapp.product.domain.Product;
import com.example.mosawebapp.product.domain.ProductRepository;
import com.example.mosawebapp.product.dto.ProductForm;
import com.example.mosawebapp.security.JwtGenerator;
import com.example.mosawebapp.validate.Validate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductServiceImpl implements ProductService{
  private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);
  @Value("${mosatiresupply.official.email}")
  private String mosaTireSupplyEmail; //mosatiresupply.official.email
  private static final String PRODUCT_NOT_EXIST = "Product does not exists";
  private final AccountService accountService;
  private final AccountRepository accountRepository;
  private final ProductRepository productRepository;
  private final ActivityLogsService activityLogsService;
  private final JwtGenerator jwtGenerator;
  private final MailService mailService;

  public ProductServiceImpl(AccountService accountService, AccountRepository accountRepository,
      ProductRepository productRepository,
      ActivityLogsService activityLogsService, JwtGenerator jwtGenerator, MailService mailService) {
    this.accountService = accountService;
    this.accountRepository = accountRepository;
    this.productRepository = productRepository;
    this.activityLogsService = activityLogsService;
    this.jwtGenerator = jwtGenerator;
    this.mailService = mailService;
  }

  @Override
  public List<Product> getAllProducts(String token) {
    accountService.validateIfAccountIsAdmin(token);

    return productRepository.findAll();
  }

  @Override
  public Product getProduct(String token, String id) {
    accountService.validateIfAccountIsAdmin(token);

    return productRepository.findById(id).orElseThrow(() -> new NotFoundException(PRODUCT_NOT_EXIST));
  }

  @Override
  @Transactional
  public Product addProduct(String token, ProductForm form) {
    accountService.validateIfAccountIsAdmin(token);
    Validate.notNull(form);

    Account admin = accountRepository.findById(jwtGenerator.getUserFromJWT(token)).orElseThrow(() -> new NotFoundException("Account does not exists"));

    logger.info("adding product with form {}", form);

    Product product = new Product(form.getName(), form.getGrossPrice(), form.getSize(), form.getPlyRating(), form.getThreadType(), form.getStocks());

    productRepository.save(product);
    mailService.sendEmailForNewProduct(mosaTireSupplyEmail, form);
    activityLogsService.addProductActivity(admin, product);
    return product;
  }

  @Override
  @Transactional
  public Product updateProduct(String id, String token, ProductForm form) {
    accountService.validateIfAccountIsAdmin(token);
    Validate.notNull(form);

    Account admin = accountRepository.findById(jwtGenerator.getUserFromJWT(token)).orElseThrow(() -> new NotFoundException("Account does not exists"));

    Product product = productRepository.findById(id).orElseThrow(() -> new NotFoundException(PRODUCT_NOT_EXIST));

    logger.info("updating product {}", product.getId());

    product.setName(form.getName());
    product.setSize(form.getSize());
    product.setPlyRating(form.getPlyRating());
    product.setThreadType(form.getThreadType());

    if(form.getGrossPrice() != null)
      product.setGrossPrice(form.getGrossPrice());

    if(form.getStocks() != null)
      product.setStocks(form.getStocks());

    productRepository.save(product);
    logger.info("product {} updated", product.getId());

    activityLogsService.updateProductActivity(admin, product);
    return product;
  }

  @Override
  public void deleteProduct(String id, String token){
    accountService.validateIfAccountIsAdmin(token);
    Account admin = accountRepository.findById(jwtGenerator.getUserFromJWT(token)).orElseThrow(() -> new NotFoundException("Account does not exists"));

    Product product = productRepository.findById(id).orElseThrow(() -> new NotFoundException(PRODUCT_NOT_EXIST));

    logger.info("deleting product {}", product.getId());

    activityLogsService.deleteProductActivity(admin, product);
    productRepository.delete(product);
  }
}
