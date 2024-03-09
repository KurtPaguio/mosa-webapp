package com.example.mosawebapp.product.threadtype.service;

import com.example.mosawebapp.product.threadtype.domain.ThreadType;
import com.example.mosawebapp.product.threadtype.dto.ThreadTypeDto;
import com.example.mosawebapp.product.threadtype.dto.ThreadTypeForm;
import java.util.List;

public interface ThreadTypeService {
  ThreadTypeDto findThreadType(String id);
  List<ThreadTypeDto> findAllThreadTypes();
  ThreadTypeDto addThreadType(String token, ThreadTypeForm form);
  ThreadTypeDto updateThreadType(String token, String id, ThreadTypeForm form);
  void deleteThreadType(String token, String id);
}
