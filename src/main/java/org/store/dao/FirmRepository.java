package org.store.dao;

import org.store.domain.Firm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FirmRepository extends JpaRepository<Firm, Long> {

    @Query(value = """
            update firm
            set deleted=:deleted
            where id=:id
            """, nativeQuery = true)
    @Modifying
    @Transactional
    void setDeleted(@Param("id") long id, @Param("deleted") boolean deleted);

    boolean existsByName(String name);

    Firm findByName(String name);

    List<Firm> findByDeleted(boolean deleted);
}
