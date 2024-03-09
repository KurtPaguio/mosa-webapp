package com.example.mosawebapp.product.threadtypedetails.service;

import com.example.mosawebapp.product.threadtypedetails.domain.ThreadTypeDetails;
import com.example.mosawebapp.product.threadtypedetails.dto.ThreadTypeDetailsDto;
import com.example.mosawebapp.product.threadtypedetails.dto.ThreadTypeDetailsForm;
import java.util.List;

public interface ThreadTypeDetailsService {
  List<ThreadTypeDetailsDto> findAllThreadTypesDetails();
  ThreadTypeDetailsDto findThreadTypeDetails(String id);
  ThreadTypeDetailsDto addThreadTypeDetails(String token, ThreadTypeDetailsForm form);
  ThreadTypeDetailsDto updateThreadTypeDetails(String token, String id, ThreadTypeDetailsForm form);
  void deleteThreadTypeDetails(String token, String id);
}
