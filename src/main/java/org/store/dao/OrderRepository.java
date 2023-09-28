package org.store.dao;

import org.store.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Transactional
    @Modifying
    @Query("update Order o set o.deleted = ?1 where o.id = ?2")
    int setDeletedById(boolean deleted, Long id);
    @Query(value = """
            select * from orders
            where id not in (select order_id from delivery where deleted=false)
            and deleted=false
            """, nativeQuery = true)
    List<Order> findWithoutDelivery();

    @Transactional
    @Modifying
    @Query(value = "update orders set deleted = ?1 where id = (select order_id from delivery where id = ?2)",
            nativeQuery = true)
    void setDeletedByDeliveryId(boolean deleted, long deliveryId);
}
