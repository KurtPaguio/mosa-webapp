package com.example.mosawebapp.product.threadtypedetails.domain;

import com.example.mosawebapp.product.brand.domain.Brand;
import com.example.mosawebapp.product.threadtype.domain.ThreadType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ThreadTypeDetailsRepository extends JpaRepository<ThreadTypeDetails, String>,
    JpaSpecificationExecutor {
  boolean existsByThreadType(ThreadType threadType);
  List<ThreadTypeDetails> findByThreadType(ThreadType type);

  @Query(value = "SELECT * FROM thread_type_details WHERE "
      + "lower(width) = :width "
      + "AND lower(aspect_ratio) = :ratio "
      + "AND lower(diameter) = :diameter "
      + "AND lower(sidewall) = :sidewall "
      + "AND lower(ply_rating) = :plyRating", nativeQuery = true)
  ThreadTypeDetails findByDetails(@Param("width") String width, @Param("ratio") String ratio, @Param("diameter") String diameter,
      @Param("sidewall") String sidewall, @Param("plyRating") String plyRating);

  @Modifying
  @Transactional
  @Query(value = "DELETE FROM thread_type_details WHERE thread_type_id = :id", nativeQuery = true)
  void deleteByThreadType(@Param("id") String id);

  @Query(value = "SELECT FROM thread_type_details WHERE stocks <= 50", nativeQuery = true)
  List<ThreadTypeDetails> findAllDetailsInCriticalStocks();
}
