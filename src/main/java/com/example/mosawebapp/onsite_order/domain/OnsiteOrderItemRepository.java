package com.example.mosawebapp.onsite_order.domain;

import com.example.mosawebapp.product.threadtype.domain.ThreadType;
import com.example.mosawebapp.product.threadtypedetails.domain.ThreadTypeDetails;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OnsiteOrderItemRepository extends JpaRepository<OnsiteOrderItem, String> {
  List<OnsiteOrderItem> findByOrder(OnsiteOrder order);
  @Query("SELECT item FROM OnsiteOrderItem item WHERE item.order = :order")
  OnsiteOrderItem findItemByOrder(@Param("order") OnsiteOrder order);
  @Query("SELECT CASE WHEN COUNT(item) > 0 THEN true ELSE false END FROM OnsiteOrderItem item WHERE item.order = :order AND item.id = :itemId")
  boolean isItemInCart(@Param("order") OnsiteOrder order, @Param("itemId") String itemId);

  @Query("SELECT item FROM OnsiteOrderItem item WHERE item.order = :order AND item.type = :type AND item.details = :details")
  OnsiteOrderItem isOrderFormAlreadyInCart(@Param("order") OnsiteOrder order, @Param("type") ThreadType type, @Param("details")
  ThreadTypeDetails details);
}
