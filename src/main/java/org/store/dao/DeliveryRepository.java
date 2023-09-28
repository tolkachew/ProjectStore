package org.store.dao;

import org.store.domain.Delivery;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    Optional<Delivery> findByOrderId(long orderId);

    boolean existsByOrderId(long orderId);

    List<Delivery> findAllByDeleted(boolean deleted, Pageable pageable);
    @Transactional
    @Modifying
    @Query("update Delivery d set d.deleted = ?1 where d.id = ?2")
    void setDeleted(boolean deleted, Long id);

}
