package com.example.mosawebapp.product.brand.service;

import com.example.mosawebapp.product.brand.domain.Brand;
import com.example.mosawebapp.product.brand.domain.BrandRepository;
import com.example.mosawebapp.product.brand.dto.BrandDto;
import com.example.mosawebapp.product.brand.dto.BrandForm;
import java.util.List;

public interface BrandService {
  List<BrandDto> findAllBrands();
  BrandDto findBrand(String id);
  Brand addBrand(String token, BrandForm form);
  Brand updateBrand(String token, String id, BrandForm form);
  void deleteBrand(String token, String id);
}
