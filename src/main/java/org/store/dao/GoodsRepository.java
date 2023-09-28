package org.store.dao;

import org.store.domain.Firm;
import org.store.domain.Goods;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GoodsRepository extends JpaRepository<Goods, Long> {
    boolean existsByFirmAndDeleted(Firm firm, boolean deleted);

    @Query(value = """
            select * 
            from goods 
            where cost between :costFrom and :costTo 
                and guarantee_term between :guaranteeFrom and :guaranteeTo
                and name like concat(\\'%\\',:namePattern,\\'%\\') 
                and model like concat(\\'%\\',:modelPattern,\\'%\\') 
                and deleted = false
            """, nativeQuery = true)
    List<Goods> findByFilter(@Param("namePattern") String namePattern,
                             @Param("modelPattern") String modelPattern,
                             @Param("costFrom") int costFrom,
                             @Param("costTo") int costTo,
                             @Param("guaranteeFrom") int guaranteeFrom,
                             @Param("guaranteeTo") int guaranteeTo,
                             Pageable pageable);

    List<Goods> findByDeleted(boolean deleted, Sort sort);

    @Query(value = """
            select * from goods
            where deleted=false and
             id in (select goods_id from goods_offers where market_id = ?1 and deleted = false)
            """, nativeQuery = true)
    List<Goods> findNotDeletedByStoreId(long storeId);

    @Query(value = """
            select * from goods
            where deleted=false and
             id not in (select goods_id from goods_offers where market_id = ?1 and deleted = false)
            """, nativeQuery = true)
    List<Goods> findNotDeletedNotFromStore(long storeId);

    @Query(value = """
            update goods
            set deleted=:deleted
            where id=:id
            """, nativeQuery = true)
    @Modifying
    void setDeleted(@Param("id") long id, @Param("deleted") boolean deleted);
}
