package com.example.mosawebapp.product.service;

import com.example.mosawebapp.account.service.AccountService;
import com.example.mosawebapp.exceptions.NotFoundException;
import com.example.mosawebapp.mail.MailService;
import com.example.mosawebapp.product.domain.Product;
import com.example.mosawebapp.product.domain.ProductRepository;
import com.example.mosawebapp.product.dto.ProductForm;
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
  private final ProductRepository productRepository;
  private final MailService mailService;

  public ProductServiceImpl(AccountService accountService, ProductRepository productRepository,
      MailService mailService) {
    this.accountService = accountService;
    this.productRepository = productRepository;
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

    logger.info("adding product with form {}", form);

    Product product = new Product(form.getName(), form.getGrossPrice(), form.getSize(), form.getPlyRating(), form.getThreadType(), form.getStocks());

    productRepository.save(product);
    mailService.sendEmailForNewProduct(mosaTireSupplyEmail, form);
    return product;
  }

  @Override
  @Transactional
  public Product updateProduct(String id, String token, ProductForm form) {
    accountService.validateIfAccountIsAdmin(token);
    Validate.notNull(form);

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

    return product;
  }

  @Override
  public void deleteProduct(String id, String token){
    accountService.validateIfAccountIsAdmin(token);

    Product product = productRepository.findById(id).orElseThrow(() -> new NotFoundException(PRODUCT_NOT_EXIST));

    logger.info("deleting product {}", product.getId());

    productRepository.delete(product);
  }
}
