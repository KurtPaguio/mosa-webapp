package com.example.mosawebapp.product.threadtype.service;

import com.example.mosawebapp.account.domain.Account;
import com.example.mosawebapp.account.domain.AccountRepository;
import com.example.mosawebapp.account.domain.UserRole;
import com.example.mosawebapp.exceptions.NotFoundException;
import com.example.mosawebapp.exceptions.ValidationException;
import com.example.mosawebapp.mail.MailService;
import com.example.mosawebapp.product.brand.domain.Brand;
import com.example.mosawebapp.product.brand.domain.BrandRepository;
import com.example.mosawebapp.product.threadtype.domain.ThreadType;
import com.example.mosawebapp.product.threadtype.domain.ThreadTypeRepository;
import com.example.mosawebapp.product.threadtype.dto.ThreadTypeDto;
import com.example.mosawebapp.product.threadtype.dto.ThreadTypeForm;
import com.example.mosawebapp.product.threadtypedetails.domain.ThreadTypeDetails;
import com.example.mosawebapp.product.threadtypedetails.domain.ThreadTypeDetailsRepository;
import com.example.mosawebapp.security.JwtGenerator;
import com.example.mosawebapp.validate.Validate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ThreadTypeServiceImpl implements ThreadTypeService{
  private static final String TYPE_NOT_EXIST = "Thread Type does not exists";
  @Value("${mosatiresupply.official.email}")
  private String MOSA_TIRE_SUPPLY_EMAIL;
  @Value("${default.blank.image.cdn}")
  private String BLANK_IMAGE;
  private final BrandRepository brandRepository;
  private final ThreadTypeRepository threadTypeRepository;
  private final ThreadTypeDetailsRepository threadTypeDetailsRepository;
  private final AccountRepository accountRepository;
  private final JwtGenerator jwtGenerator;
  private final MailService mailService;

  @Autowired
  public ThreadTypeServiceImpl(BrandRepository brandRepository,
      ThreadTypeRepository threadTypeRepository,
      ThreadTypeDetailsRepository threadTypeDetailsRepository, AccountRepository accountRepository,
      JwtGenerator jwtGenerator, MailService mailService) {
    this.brandRepository = brandRepository;
    this.threadTypeRepository = threadTypeRepository;
    this.threadTypeDetailsRepository = threadTypeDetailsRepository;
    this.accountRepository = accountRepository;
    this.jwtGenerator = jwtGenerator;
    this.mailService = mailService;
  }

  @Override
  public ThreadTypeDto findThreadType(String id) {
    ThreadType threadType = threadTypeRepository.findById(id).orElseThrow(() -> new NotFoundException(TYPE_NOT_EXIST));
    List<ThreadTypeDetails> details = threadTypeDetailsRepository.findByThreadType(threadType);

    if(details.isEmpty()){
      return new ThreadTypeDto(threadType);
    }

    return new ThreadTypeDto(threadType, details);
  }

  @Override
  public List<ThreadTypeDto> findAllThreadTypes() {
    List<ThreadType> types = threadTypeRepository.findAll();

    List<ThreadTypeDto> dtos = new ArrayList<>();
    for(ThreadType type: types){
      List<ThreadTypeDetails> details = threadTypeDetailsRepository.findByThreadType(type);

      if(details.isEmpty()){
        dtos.add(new ThreadTypeDto(type));
      } else {
        dtos.add(new ThreadTypeDto(type, details));
      }
    }

    return dtos;
  }

  @Override
  public ThreadTypeDto addThreadType(String token, ThreadTypeForm form) {
    Account account = getAccountFromToken(token);
    validateIfAccountIsAdmin(account);
    Validate.notNull(form);

    if(form.getImageUrl().isEmpty()){
      form.setImageUrl(BLANK_IMAGE);
    }

    Brand brand = brandRepository.findByNameIgnoreCase(form.getBrand());

    if(brand == null){
      brand = brandRepository.findById(form.getBrand()).orElseThrow(() -> new NotFoundException("Brand does not exists"));
    }

    ThreadType threadType = new ThreadType(form.getType(), form.getImageUrl(), form.getDescription(), brand);

    mailService.sendEmailForThreadType(MOSA_TIRE_SUPPLY_EMAIL, brand, threadType, "Added");
    threadTypeRepository.save(threadType);

    return new ThreadTypeDto(threadType);
  }

  @Override
  public ThreadTypeDto updateThreadType(String token, String id, ThreadTypeForm form) {
    Account account = getAccountFromToken(token);
    validateIfAccountIsAdmin(account);
    Validate.notNull(form);

    if(form.getImageUrl().isEmpty()){
      form.setImageUrl(BLANK_IMAGE);
    }

    ThreadType threadType = threadTypeRepository.findById(id).orElseThrow(() -> new NotFoundException("Thread type does not exists"));
    Brand brand = brandRepository.findByNameIgnoreCase(form.getBrand());

    if(brand == null){
      brand = brandRepository.findById(form.getBrand()).orElseThrow(() -> new NotFoundException("Brand does not exists"));
    }

    threadType.setBrand(brand);
    threadType.setType(form.getType());
    threadType.setImageUrl(form.getImageUrl());
    threadType.setDescription(form.getDescription());

    mailService.sendEmailForThreadType(MOSA_TIRE_SUPPLY_EMAIL, brand, threadType, "Updated");
    threadTypeRepository.save(threadType);

    return new ThreadTypeDto(threadType);
  }

  @Override
  public void deleteThreadType(String token, String id) {
    Account account = getAccountFromToken(token);
    validateIfAccountIsAdmin(account);

    ThreadType threadType = threadTypeRepository.findById(id).orElseThrow(() -> new NotFoundException("Thread type does not exists"));

    mailService.sendEmailForThreadType(MOSA_TIRE_SUPPLY_EMAIL, threadType.getBrand(),threadType, "Deleted");
    threadTypeRepository.delete(threadType);
  }

  private Account getAccountFromToken(String token){
    String id = jwtGenerator.getUserFromJWT(token);

    return accountRepository.findById(id).orElseThrow(() -> new NotFoundException("Account does not exists"));
  }

  private void validateIfAccountIsAdmin(Account account){
    if(account.getUserRole() != UserRole.ADMINISTRATOR){
      throw new ValidationException("Only Administrators can use this feature");

    }
  }
}
