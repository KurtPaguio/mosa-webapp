package com.example.mosawebapp.product.threadtypedetails.domain;

import com.example.mosawebapp.product.threadtype.domain.ThreadType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ThreadTypeDetailsRepository extends JpaRepository<ThreadTypeDetails, String>,
    JpaSpecificationExecutor {

  List<ThreadTypeDetails> findByThreadType(ThreadType type);
}
