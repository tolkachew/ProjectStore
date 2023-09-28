package org.store.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.store.dao.GoodsRepository;
import org.store.dao.OnlineStoresRepository;
import org.store.domain.Goods;
import org.store.domain.OnlineStore;
import org.store.dto.GoodsDto;
import org.store.dto.OnlineStoreDto;
import org.store.dto.OnlineStoreFilterDto;
import org.store.dto.Payment;
import org.store.service.OnlineStoresService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OnlineStoresServiceImpl implements OnlineStoresService {

    private static final String EMPTY_STRING = "";

    private static final int DEFAULT_PAGE_SIZE = 50;

    private static final int DEFAULT_PAGE_NUM = 0;

    private static final String DEFAULT_SORT_FIELD = "Internet Address";

    private static final Map<String, String> map;

    private final OnlineStoresRepository storesRepository;
    private final GoodsRepository goodsRepository;

    static {
        map = new HashMap<>();
        map.put("Internet Address", "internetAddress");
        map.put("Is delivery paid", "deliveryPaid");
    }

    @Override
    public OnlineStoreDto getById(long id) {
        Optional<OnlineStore> store = storesRepository.findById(id);
        return store.map(OnlineStoreDto::new).orElseGet(OnlineStoreDto::new);
    }

    @Override
    public List<OnlineStoreDto> getAll() {
        Example<OnlineStore> example = Example.of(new OnlineStore(null, null, null, false));

        return convertToDtoList(storesRepository.findAll(example));
    }

    @Override
    public List<OnlineStoreDto> getAll(OnlineStoreFilterDto filterDto) {
        correctFilterDto(filterDto);
        Pageable pageable = PageRequest.of(filterDto.getPageNum(),
                filterDto.getPageSize(), Sort.by(map.get(filterDto.getSortBy())));
        List<OnlineStore> stores;
        if(filterDto.getPayment().equals(Payment.BOTH.toString())){
             stores = storesRepository
                     .findByDeletedAndInternetAddressContaining(false, filterDto.getInternetAddress(), pageable);
            return convertToDtoList(stores);
        }
        stores = storesRepository
                .findByDeliveryPaidAndDeletedAndInternetAddressContaining(
                        filterDto.getPayment().equals(Payment.IS_PAID.toString()),
                        false,
                        filterDto.getInternetAddress(), pageable);
        return convertToDtoList(stores);
    }

    @Override
    public Map<OnlineStoreDto, List<GoodsDto>> getStoreToGoodsMapping() {
        List<OnlineStoreDto> stores = getAll();
        Map<OnlineStoreDto, List<GoodsDto>> map = new HashMap<>(stores.size());
        for(OnlineStoreDto onlineStoreDto : stores){
            List<GoodsDto> goodsDtoList = convertToGoodsDtoList(goodsRepository
                    .findNotDeletedByStoreId(onlineStoreDto.getId()));
            map.put(onlineStoreDto, goodsDtoList);
        }
        return map;
    }

    @Override
    public List<String> getSortingList(String firstElement) {
        List<String> list = new ArrayList<>(map.size());
        if(map.containsKey(firstElement)) {
            list.add(firstElement);
            for(String key : map.keySet()){
                if(!key.equals(firstElement)){
                    list.add(key);
                }
            }
        }else {
            list.addAll(map.keySet());
        }

        return list;
    }

    @Override
    @Transactional
    public void save(OnlineStoreDto storeDto) {
        Long id;
        if(storeDto.getId() == null || storeDto.getId() == 0){
            Optional<OnlineStore> store = storesRepository.findOne(Example.of(
                    new OnlineStore(null, storeDto.getInternetAddress(), null, true)));
            if(store.isPresent()){
                id = store.get().getId();
                storesRepository.setDeleted(id, false);
            }else {
                OnlineStore onlineStore = new OnlineStore(null,
                        storeDto.getInternetAddress(),
                        "Yes".equals(storeDto.getDeliveryPaid()), false);
                id = storesRepository.save(onlineStore).getId();
            }
        }else{
            OnlineStore onlineStore = new OnlineStore(storeDto.getId(),
                    storeDto.getInternetAddress(),
                    "Yes".equals(storeDto.getDeliveryPaid()),
                    false);
            id = storesRepository.save(onlineStore).getId();
        }
        storeDto.setId(id);
    }

    @Override
    @Transactional
    public void delete(long id) {
        storesRepository.setDeleted(id, true);
    }

    @Override
    public void addGoodsToStore(long storeId, Long[] gIds) {
        for(Long gId : gIds){
            storesRepository.addGoodsToStore(storeId, gId);
        }
    }

    @Override
    public void addGoodsToStore(long storeId, long goodsId) {
        storesRepository.addGoodsToStore(storeId, goodsId);
    }

    @Override
    public void removeGoodsFromStore(long storeId, long goodsId) {
        storesRepository.removeGoodsFromStore(storeId, goodsId);
    }

    private void correctFilterDto(OnlineStoreFilterDto filterDto){
        if(filterDto.getInternetAddress() == null){
            filterDto.setInternetAddress(EMPTY_STRING);
        }
        if(filterDto.getPayment() == null){
            filterDto.setPayment(Payment.BOTH.toString());
        }
        if(filterDto.getPageSize() <= 0 || filterDto.getPageNum() < 0){
            filterDto.setPageSize(DEFAULT_PAGE_SIZE);
            filterDto.setPageNum(DEFAULT_PAGE_NUM);
        }
        if(!map.containsKey(filterDto.getSortBy())){
            filterDto.setSortBy(DEFAULT_SORT_FIELD);
        }
    }

    private List<OnlineStoreDto> convertToDtoList(List<OnlineStore> list){
        return list.stream().map(os -> new OnlineStoreDto(os.getId(),
                    os.getInternetAddress(), os.getDeliveryPaid() ? "yes" : "no")).toList();
    }

    private List<GoodsDto> convertToGoodsDtoList(List<Goods> goods){
        return goods.stream().map(GoodsDto::new).collect(Collectors.toList());
    }
}
