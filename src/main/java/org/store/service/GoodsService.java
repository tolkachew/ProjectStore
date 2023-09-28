package org.store.service;

import org.store.dto.GoodsDto;
import org.store.dto.GoodsDtoContainer;
import org.store.dto.GoodsFilterDto;

import java.util.List;

public interface GoodsService {

    List<GoodsDto> getGoodsFromStore(long storeId);
    List<GoodsDto> getGoodsNotFromStore(long storeId);

    GoodsDtoContainer getAll();

    GoodsDtoContainer getAllByFilter(GoodsFilterDto dto);

    List<String> getSortingNames(String startWith);

    /**
     * if id is 0 creates new entity, otherwise updates existing entity
     * */
    void save(GoodsDto dto);

    void delete(long id);

    GoodsDto getById(long id);

    //void update(GoodsDto dto);
}
