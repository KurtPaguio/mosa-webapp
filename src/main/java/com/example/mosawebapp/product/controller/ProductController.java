package com.example.mosawebapp.product.controller;

import com.example.mosawebapp.account.dto.AccountDto;
import com.example.mosawebapp.account.dto.AccountUpdateForm;
import com.example.mosawebapp.apiresponse.ApiErrorResponse;
import com.example.mosawebapp.apiresponse.ApiObjectResponse;
import com.example.mosawebapp.apiresponse.ApiResponse;
import com.example.mosawebapp.exceptions.NotFoundException;
import com.example.mosawebapp.exceptions.SecurityException;
import com.example.mosawebapp.exceptions.TokenException;
import com.example.mosawebapp.exceptions.ValidationException;
import com.example.mosawebapp.product.dto.ProductDto;
import com.example.mosawebapp.product.dto.ProductForm;
import com.example.mosawebapp.product.service.ProductService;
import com.example.mosawebapp.security.JwtGenerator;
import com.example.mosawebapp.security.domain.TokenBlacklistingService;
import com.example.mosawebapp.utils.DateTimeFormatter;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/product")
public class ProductController {
  private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
  private static final String BEARER = "Bearer ";
  private static final String TOKEN_INVALID = "Token Invalid/Expired";
  private final ProductService productService;
  private final TokenBlacklistingService tokenBlacklistingService;
  private final JwtGenerator jwtGenerator;

  public ProductController(ProductService productService, TokenBlacklistingService tokenBlacklistingService,
      JwtGenerator jwtGenerator) {
    this.productService = productService;
    this.tokenBlacklistingService = tokenBlacklistingService;
    this.jwtGenerator = jwtGenerator;
  }

  @GetMapping(value="/getAllProducts")
  public ResponseEntity<?> getProducts(@RequestHeader("Authorization") String header){
    logger.info("getting all products");
    String token = header.replace(BEARER, "");

    try{
      if(!jwtGenerator.isTokenValid(token) || token.isEmpty() || tokenBlacklistingService.isTokenBlacklisted(token)){
        throw new TokenException(TOKEN_INVALID);
      }

      return ResponseEntity.ok(ProductDto.buildFromEntities(productService.getAllProducts(token)));
    } catch(SecurityException se){
      return new ResponseEntity<>(new ApiErrorResponse(
          DateTimeFormatter.get_MMDDYYY_Format(new Date()),"500", HttpStatus.INTERNAL_SERVER_ERROR, se.getMessage()),
          HttpStatus.INTERNAL_SERVER_ERROR);
    } catch(NotFoundException | NullPointerException ne){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"400", HttpStatus.NOT_FOUND, ne.getMessage()),
          HttpStatus.BAD_REQUEST);
    } catch(TokenException te){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"401", HttpStatus.UNAUTHORIZED, te.getMessage()),
          HttpStatus.UNAUTHORIZED);
    } catch(ValidationException ve){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"400", HttpStatus.BAD_REQUEST, ve.getMessage()),
          HttpStatus.BAD_REQUEST);
    }
  }

  @GetMapping(value="/getProduct/{productId}")
  public ResponseEntity<?> getProduct(@PathVariable("productId") String id, @RequestHeader("Authorization") String header){
    logger.info("getting product {}", id);
    String token = header.replace(BEARER, "");

    try{
      validateTokenValidity(token);

      ProductDto dto = ProductDto.buildFromEntity(productService.getProduct(token, id));

      logger.info("done fetching product {}}", dto.getName());
      return ResponseEntity.ok(dto);
    } catch(SecurityException se){
      return new ResponseEntity<>(new ApiErrorResponse(
          DateTimeFormatter.get_MMDDYYY_Format(new Date()),"500", HttpStatus.INTERNAL_SERVER_ERROR, se.getMessage()),
          HttpStatus.INTERNAL_SERVER_ERROR);
    } catch(NotFoundException | NullPointerException ne){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"400", HttpStatus.NOT_FOUND, ne.getMessage()),
          HttpStatus.BAD_REQUEST);
    } catch(TokenException te){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"401", HttpStatus.UNAUTHORIZED, te.getMessage()),
          HttpStatus.UNAUTHORIZED);
    } catch(ValidationException ve){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"400", HttpStatus.BAD_REQUEST, ve.getMessage()),
          HttpStatus.BAD_REQUEST);
    }
  }


  @PostMapping(value="/addProduct")
  public ResponseEntity<?> addProduct(@RequestHeader("Authorization") String header, @RequestBody ProductForm form){
    String token = header.replace(BEARER, "");

    try{
      validateTokenValidity(token);

      ProductDto dto = ProductDto.buildFromEntity(productService.addProduct(token, form));

      logger.info("Product {} added", dto.getName());
      return ResponseEntity.ok(new ApiObjectResponse(HttpStatus.CREATED, "Product " + dto.getName() + " created", dto));
    } catch(SecurityException se){
      return new ResponseEntity<>(new ApiErrorResponse(
          DateTimeFormatter.get_MMDDYYY_Format(new Date()),"500", HttpStatus.INTERNAL_SERVER_ERROR, se.getMessage()),
          HttpStatus.INTERNAL_SERVER_ERROR);
    } catch(NotFoundException | NullPointerException ne){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"400", HttpStatus.NOT_FOUND, ne.getMessage()),
          HttpStatus.BAD_REQUEST);
    } catch(TokenException te){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"401", HttpStatus.UNAUTHORIZED, te.getMessage()),
          HttpStatus.UNAUTHORIZED);
    } catch(ValidationException ve){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"400", HttpStatus.BAD_REQUEST, ve.getMessage()),
          HttpStatus.BAD_REQUEST);
    }
  }

  @PutMapping(value="/updateProduct/{productId}")
  public ResponseEntity<?> updateAccount(@PathVariable("productId") String id, @RequestBody ProductForm form, @RequestHeader("Authorization") String header){
    logger.info("updating product with form {}", form);
    String token = header.replace(BEARER, "");

    try {
      validateTokenValidity(token);

      ProductDto dto = ProductDto.buildFromEntity(productService.updateProduct(id, token, form));

      logger.info("product {} updated", dto.getName());
      return ResponseEntity.ok(new ApiObjectResponse(HttpStatus.OK, "Product " + dto.getName() + " updated " , dto));
    } catch(SecurityException se){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"500", HttpStatus.INTERNAL_SERVER_ERROR, se.getMessage()),
          HttpStatus.INTERNAL_SERVER_ERROR);
    } catch(NotFoundException | NullPointerException ne){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"404", HttpStatus.NOT_FOUND, ne.getMessage()),
          HttpStatus.BAD_REQUEST);
    } catch(TokenException te){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"401", HttpStatus.UNAUTHORIZED, te.getMessage()),
          HttpStatus.UNAUTHORIZED);
    } catch(ValidationException ve){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"400", HttpStatus.BAD_REQUEST, ve.getMessage()),
          HttpStatus.BAD_REQUEST);
    }
  }

  @DeleteMapping(value="/deleteProduct/{productId}")
  public ResponseEntity<?> deleteAccount(@PathVariable("productId") String id, @RequestHeader("Authorization") String header){
    String token = header.replace(BEARER, "");

    try{
      validateTokenValidity(token);

      productService.deleteProduct(id, token);

      logger.info("done deleting product");
      return new ResponseEntity<>(new ApiResponse("Product Deleted Successfully", HttpStatus.OK), HttpStatus.OK);
    } catch(SecurityException se){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"500", HttpStatus.INTERNAL_SERVER_ERROR, se.getMessage()),
          HttpStatus.INTERNAL_SERVER_ERROR);
    } catch(NotFoundException | NullPointerException ne){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"404", HttpStatus.NOT_FOUND, ne.getMessage()),
          HttpStatus.BAD_REQUEST);
    } catch(TokenException te){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"401", HttpStatus.UNAUTHORIZED, te.getMessage()),
          HttpStatus.UNAUTHORIZED);
    } catch(ValidationException ve){
      return new ResponseEntity<>(new ApiErrorResponse(DateTimeFormatter.get_MMDDYYY_Format(new Date()),"400", HttpStatus.BAD_REQUEST, ve.getMessage()),
          HttpStatus.BAD_REQUEST);
    }
  }

  private void validateTokenValidity(String token){
    if(!jwtGenerator.isTokenValid(token) || token.isEmpty() || tokenBlacklistingService.isTokenBlacklisted(token)){
      throw new TokenException(TOKEN_INVALID);
    }
  }
}
