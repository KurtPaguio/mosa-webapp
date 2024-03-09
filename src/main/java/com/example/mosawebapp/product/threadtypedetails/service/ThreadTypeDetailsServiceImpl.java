package com.example.mosawebapp.product.threadtypedetails.service;

import com.example.mosawebapp.account.domain.Account;
import com.example.mosawebapp.account.domain.AccountRepository;
import com.example.mosawebapp.account.domain.UserRole;
import com.example.mosawebapp.exceptions.NotFoundException;
import com.example.mosawebapp.exceptions.ValidationException;
import com.example.mosawebapp.mail.MailService;
import com.example.mosawebapp.product.brand.domain.BrandRepository;
import com.example.mosawebapp.product.threadtype.domain.ThreadType;
import com.example.mosawebapp.product.threadtype.domain.ThreadTypeRepository;
import com.example.mosawebapp.product.threadtypedetails.domain.ThreadTypeDetails;
import com.example.mosawebapp.product.threadtypedetails.domain.ThreadTypeDetailsRepository;
import com.example.mosawebapp.product.threadtypedetails.dto.ThreadTypeDetailsDto;
import com.example.mosawebapp.product.threadtypedetails.dto.ThreadTypeDetailsForm;
import com.example.mosawebapp.security.JwtGenerator;
import com.example.mosawebapp.validate.Validate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ThreadTypeDetailsServiceImpl implements ThreadTypeDetailsService{
  private static final String TYPE_DETAILS_NOT_EXIST = "Thread Type Detai;s does not exists";
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
  public ThreadTypeDetailsServiceImpl(BrandRepository brandRepository,
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
  public List<ThreadTypeDetailsDto> findAllThreadTypesDetails() {
    List<ThreadTypeDetails> details = threadTypeDetailsRepository.findAll();

    List<ThreadTypeDetailsDto> dto = new ArrayList<>();

    for(ThreadTypeDetails detail: details){
      ThreadType threadType = threadTypeRepository.findTypeId(detail.getThreadType().getId());

      if(threadType == null){
        dto.add(ThreadTypeDetailsDto.buildFromEntity(detail));
      } else{
        dto.add(new ThreadTypeDetailsDto(threadType, detail));
      }
    }

    return dto;
  }

  @Override
  public ThreadTypeDetailsDto findThreadTypeDetails(String id) {
    ThreadTypeDetails details = threadTypeDetailsRepository.findById(id).orElseThrow(() -> new NotFoundException(TYPE_DETAILS_NOT_EXIST));
    ThreadType threadType = threadTypeRepository.findTypeId(details.getThreadType().getId());

    if(threadType == null){
      return ThreadTypeDetailsDto.buildFromEntity(details);
    }

    return new ThreadTypeDetailsDto(threadType, details);
  }

  @Override
  public ThreadTypeDetailsDto addThreadTypeDetails(String token, ThreadTypeDetailsForm form) {
    Account account = getAccountFromToken(token);
    validateIfAccountIsAdmin(account);
    Validate.notNull(form);
    validateForm(form);

    ThreadType threadType = threadTypeRepository.findByTypeIgnoreCase(form.getThreadType());

    if(threadType == null){
      threadType = threadTypeRepository.findById(form.getThreadType()).orElseThrow(() -> new NotFoundException("Thread Type does not exists"));
    }

    ThreadTypeDetails details = new ThreadTypeDetails(form.getWidth(), form.getAspectRatio(), form.getDiameter(), form.getPrice(), form.getStocks(), threadType);

    mailService.sendEmailForThreadTypeDetails(MOSA_TIRE_SUPPLY_EMAIL, details, "Added");
    threadTypeDetailsRepository.save(details);


    return new ThreadTypeDetailsDto(threadType, details);
  }

  @Override
  public ThreadTypeDetailsDto updateThreadTypeDetails(String token, String id,
      ThreadTypeDetailsForm form) {
    Account account = getAccountFromToken(token);
    validateIfAccountIsAdmin(account);
    Validate.notNull(form);
    validateForm(form);

    ThreadTypeDetails details = threadTypeDetailsRepository.findById(id).orElseThrow(() -> new NotFoundException("Thread Type Details does not exists"));
    ThreadType threadType = threadTypeRepository.findByTypeIgnoreCase(form.getThreadType());

    if(threadType == null){
      threadType = threadTypeRepository.findById(form.getThreadType()).orElseThrow(() -> new NotFoundException("Thread Type does not exists"));
    }

    details.setWidth(form.getWidth());
    details.setAspectRatio(form.getAspectRatio());
    details.setDiameter(form.getDiameter());
    details.setPrice(form.getPrice());
    details.setThreadType(threadType);
    if(form.getStocks() != null){
      details.setStocks(form.getStocks());
    }

    mailService.sendEmailForThreadTypeDetails(MOSA_TIRE_SUPPLY_EMAIL, details, "Updated");
    threadTypeDetailsRepository.save(details);

    return new ThreadTypeDetailsDto(threadType, details);
  }

  @Override
  public void deleteThreadTypeDetails(String token, String id) {
    Account account = getAccountFromToken(token);
    validateIfAccountIsAdmin(account);

    ThreadTypeDetails details = threadTypeDetailsRepository.findById(id).orElseThrow(() -> new NotFoundException("Thread Type Details does not exists"));

    mailService.sendEmailForThreadTypeDetails(MOSA_TIRE_SUPPLY_EMAIL, details, "Deleted");
    threadTypeDetailsRepository.delete(details);
  }

  private void validateForm(ThreadTypeDetailsForm form){
    if(form.getPrice() < 0){
      throw new ValidationException("Price must be greater than 0");
    }

    if(form.getStocks() != null && form.getStocks() < 0){
      throw new ValidationException("Stock quantity must be greater than or equal to 0");
    }
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
