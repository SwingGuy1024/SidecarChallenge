package org.openapitools.repositories;

import java.time.OffsetDateTime;
import java.util.Collection;
import org.openapitools.entity.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 2/20/18
 * <p>Time: 5:47 PM
 *
 * @author Miguel Mu\u00f1oz
 */
@Repository
public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Integer> {
  Collection<CustomerOrder> findByOrderTimeAfterAndOrderTimeBeforeOrderByOrderTime(OffsetDateTime startOfRange, OffsetDateTime endOfRange);
  
  Collection<CustomerOrder> findByOrderTimeAfterAndOrderTimeBeforeAndCompleteOrderByOrderTime(
      OffsetDateTime startOfRange,
      OffsetDateTime endOfRange, 
      Boolean complete
  );

  Collection<CustomerOrder> findByOrderTimeAfter(OffsetDateTime orderTime);
}
