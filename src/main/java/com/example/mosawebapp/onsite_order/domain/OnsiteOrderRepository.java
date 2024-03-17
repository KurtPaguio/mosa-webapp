package com.example.mosawebapp.onsite_order.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OnsiteOrderRepository extends JpaRepository<OnsiteOrder, String> {
  @Query("SELECT ord FROM OnsiteOrder ord WHERE ord.id = :id AND ord.isActive = :isActive ORDER BY ord.dateCreated DESC")
  OnsiteOrder findByIdAndIsActiveLatest(@Param("id") String id, @Param("isActive") boolean isActive);

  @Query("SELECT ord FROM OnsiteOrder ord WHERE ord.isActive = :isActive ORDER BY ord.dateCreated DESC")
  OnsiteOrder findLatestActiveOrder(@Param("isActive") boolean isActive);
}
