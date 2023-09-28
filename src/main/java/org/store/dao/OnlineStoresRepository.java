package org.store.dao;

import org.store.domain.OnlineStore;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OnlineStoresRepository extends JpaRepository<OnlineStore, Long> {
    List<OnlineStore> findByDeletedAndInternetAddressContaining(boolean deleted, String internetAddress, Pageable pageable);

    List<OnlineStore> findByDeliveryPaidAndDeletedAndInternetAddressContaining(boolean deliveryPaid, boolean deleted, String internetAddress, Pageable pageable);

    @Query(value = """
            update internet_markets
            set deleted=:deleted
            where id=:id
            """, nativeQuery = true)
    @Modifying
    void setDeleted(@Param("id") long id, @Param("deleted") boolean deleted);

    @Query(value = """
            insert into goods_offers (market_id, goods_id) values
            (?1, ?2)
            """, nativeQuery = true)
    @Modifying
    @Transactional
    void addGoodsToStore(long storeId, long goodsId);

    @Query(value = """
            delete from goods_offers where market_id=?1 and goods_id=?2
            """, nativeQuery = true)
    @Modifying
    @Transactional
    void removeGoodsFromStore(long storeId, long goodsId);
}
