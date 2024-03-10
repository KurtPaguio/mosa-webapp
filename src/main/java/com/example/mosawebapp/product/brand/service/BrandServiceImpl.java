package com.example.mosawebapp.product.brand.service;


import com.example.mosawebapp.account.domain.Account;
import com.example.mosawebapp.account.domain.AccountRepository;
import com.example.mosawebapp.account.domain.UserRole;
import com.example.mosawebapp.exceptions.NotFoundException;
import com.example.mosawebapp.exceptions.ValidationException;
import com.example.mosawebapp.logs.service.ActivityLogsService;
import com.example.mosawebapp.mail.MailService;
import com.example.mosawebapp.product.brand.domain.Brand;
import com.example.mosawebapp.product.brand.domain.BrandRepository;
import com.example.mosawebapp.product.brand.dto.BrandDto;
import com.example.mosawebapp.product.brand.dto.BrandForm;
import com.example.mosawebapp.product.threadtype.domain.ThreadType;
import com.example.mosawebapp.product.threadtype.domain.ThreadTypeRepository;
import com.example.mosawebapp.product.threadtype.dto.ThreadTypeDtoV2;
import com.example.mosawebapp.security.JwtGenerator;
import com.example.mosawebapp.validate.Validate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BrandServiceImpl implements BrandService {
  private static final String BRAND_NOT_EXIST = "Brand does not exists";
  private static final String ADDED = "Added";
  private static final String UPDATED = "Updated";
  private static final String DELETED = "Deleted";
  @Value("${mosatiresupply.official.email}")
  private String MOSA_TIRE_SUPPLY_EMAIL;
  private final BrandRepository brandRepository;
  private final ThreadTypeRepository threadTypeRepository;
  private final JwtGenerator jwtGenerator;
  private final AccountRepository accountRepository;
  private final MailService mailService;
  private final ActivityLogsService activityLogsService;

  @Autowired
  public BrandServiceImpl(BrandRepository brandRepository,
      ThreadTypeRepository threadTypeRepository, JwtGenerator jwtGenerator,
      AccountRepository accountRepository, MailService mailService,
      ActivityLogsService activityLogsService) {
    this.brandRepository = brandRepository;
    this.threadTypeRepository = threadTypeRepository;
    this.jwtGenerator = jwtGenerator;
    this.accountRepository = accountRepository;
    this.mailService = mailService;
    this.activityLogsService = activityLogsService;
  }


  @Override
  public List<BrandDto> findAllBrands() {
    List<BrandDto> dtos = new ArrayList<>();

    List<Brand> brands = brandRepository.findAll();

    for(Brand brand: brands){
      List<ThreadType> types = threadTypeRepository.findByBrand(brand);
      BrandDto dto = new BrandDto(brand, ThreadTypeDtoV2.buildFromEntities(types));

      dtos.add(dto);
    }

    return dtos;
  }

  @Override
  public BrandDto findBrand(String id) {
    Brand brand = brandRepository.findById(id).orElseThrow(() -> new NotFoundException(BRAND_NOT_EXIST));
    List<ThreadType> types = threadTypeRepository.findByBrand(brand);

    return new BrandDto(brand, ThreadTypeDtoV2.buildFromEntities(types));
  }

  @Override
  public Brand addBrand(String token, BrandForm form) {
    Account account = getAccountFromToken(token);

    validateIfAccountIsAdmin(account);
    Validate.notNull(form);

    Brand brand = new Brand(form.getBrandName());
    brandRepository.save(brand);
    mailService.sendEmailForBrand(MOSA_TIRE_SUPPLY_EMAIL, brand, ADDED);
    activityLogsService.brandActivity(account, brand, ADDED);
    return brand;
  }

  @Override
  public Brand updateBrand(String token, String id, BrandForm form) {
    Account account = getAccountFromToken(token);

    validateIfAccountIsAdmin(account);
    Validate.notNull(form);

    Brand brand = brandRepository.findById(id).orElseThrow(() -> new NotFoundException(BRAND_NOT_EXIST));
    brand.setName(form.getBrandName());

    brandRepository.save(brand);
    mailService.sendEmailForBrand(MOSA_TIRE_SUPPLY_EMAIL, brand,UPDATED);
    activityLogsService.brandActivity(account, brand, UPDATED);
    return brand;
  }

  @Override
  public void deleteBrand(String token, String id) {
    Account account = getAccountFromToken(token);

    validateIfAccountIsAdmin(account);

    Brand brand = brandRepository.findById(id).orElseThrow(() -> new NotFoundException(BRAND_NOT_EXIST));

    mailService.sendEmailForBrand(MOSA_TIRE_SUPPLY_EMAIL, brand, DELETED);
    activityLogsService.brandActivity(account, brand, DELETED);
    brandRepository.delete(brand);
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
