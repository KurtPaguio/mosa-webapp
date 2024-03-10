package com.example.mosawebapp.product.threadtype.domain;

import com.example.mosawebapp.product.brand.domain.Brand;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ThreadTypeRepository extends JpaRepository<ThreadType, String>,
    JpaSpecificationExecutor {

  List<ThreadType> findByBrand(Brand brand);
  @Query(value = "SELECT * FROM thread_type WHERE id = :id", nativeQuery = true)
  ThreadType findTypeId(@Param("id") String id);
  ThreadType findByTypeIgnoreCase(String type);
}
