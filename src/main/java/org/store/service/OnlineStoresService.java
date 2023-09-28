package org.store.service;

import org.store.dto.GoodsDto;
import org.store.dto.OnlineStoreDto;
import org.store.dto.OnlineStoreFilterDto;

import java.util.List;
import java.util.Map;

public interface OnlineStoresService {
    List<OnlineStoreDto> getAll(OnlineStoreFilterDto filterDto);

    List<OnlineStoreDto> getAll();

    Map<OnlineStoreDto, List<GoodsDto>> getStoreToGoodsMapping();

    List<String> getSortingList(String firstElement);

    void save(OnlineStoreDto storeDto);

    void delete(long id);

    OnlineStoreDto getById(long id);

    void addGoodsToStore(long storeId, Long[] gIds);

    void addGoodsToStore(long storeId, long goodsId);

    void removeGoodsFromStore(long storeId, long goodsId);
}
