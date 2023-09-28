package org.store.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.store.dao.FirmRepository;
import org.store.dao.GoodsRepository;
import org.store.domain.Firm;
import org.store.domain.Goods;
import org.store.dto.GoodsDto;
import org.store.dto.GoodsDtoContainer;
import org.store.dto.GoodsFilterDto;
import org.store.service.GoodsService;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GoodsServiceImpl implements GoodsService {

    private final GoodsRepository goodsRepository;

    private final FirmRepository firmRepository;

    /**
     * maps beautiful names to properties' names
     */
    private static final Map<String, String> map;

    private static final int DEFAULT_PAGE_SIZE = 50;
    private static final int DEFAULT_PAGE_NUM = 0;
    /**
     *  Field's name in the table
     *  must be in the map
     *  */
    private static final String DEFAULT_SORT_FIELD = "name";

    private static final String EMPTY_STRING = "";

    static {
        map = new HashMap<>();
        map.put("Name", "name");
        map.put("Firm's name", EMPTY_STRING);
        map.put("Model", "model");
        map.put("Cost", "cost");
        map.put("Guarantee term", "guarantee_term");
    }

    @Override
    public GoodsDtoContainer getAll() {
        List<Goods> goods = goodsRepository.findByDeleted(false, Sort.by(DEFAULT_SORT_FIELD));
        List<GoodsDto> dtos = convertToDtoList(goods);
        GoodsDtoContainer container = new GoodsDtoContainer();
        container.setGoodsDtos(dtos);
        GoodsFilterDto filterDto = new GoodsFilterDto();
        filterDto.setSortBy(map.entrySet().stream().filter(e -> e.getValue()
                .equals(DEFAULT_SORT_FIELD)).findFirst().orElse(new AbstractMap
                .SimpleEntry<>(DEFAULT_SORT_FIELD, DEFAULT_SORT_FIELD)).getKey());
        filterDto.setPageNum(DEFAULT_PAGE_NUM);
        filterDto.setPageSize(DEFAULT_PAGE_SIZE);

        container.setFilterDto(filterDto);

        return container;
    }

    @Override
    public GoodsDtoContainer getAllByFilter(GoodsFilterDto dto) {
        Sort sort;
        int costFrom = parseCost(dto.getCostFrom());
        int costTo = parseCost(dto.getCostTo());
        if(costFrom < 0 || costTo <= 0){
            costFrom = 0;
            costTo = Integer.MAX_VALUE;
        }
        if(costFrom > costTo){
            int buf = costFrom;
            costFrom = costTo;
            costTo = buf;
        }
        correctFilterDto(dto);
        Pageable pageable;
        List<Goods> result;
        String sortDtoStr = DEFAULT_SORT_FIELD;
        if(!"Firm's name".equals(dto.getSortBy())) {
            if(map.containsKey(dto.getSortBy())){
                sortDtoStr = map.get(dto.getSortBy());
            }
            sort = Sort.by(sortDtoStr);
            pageable = PageRequest.of(dto.getPageNum(), dto.getPageSize(), sort);
            result = goodsRepository.findByFilter(dto.getName(), dto.getModel(), costFrom,
                    costTo, dto.getGuaranteeFrom(), dto.getGuaranteeTo(), pageable);
        }else {
            sort = Sort.by(sortDtoStr);
            pageable = PageRequest.of(dto.getPageNum(), dto.getPageSize(), sort);
            result = goodsRepository.findByFilter(dto.getName(),
                    dto.getModel(), costFrom, costTo, dto.getGuaranteeFrom(), dto.getGuaranteeTo(),  pageable);
            result.sort(Comparator.comparing(g -> g.getFirm().getName()));
        }
        if(!dto.getFirmName().isEmpty()){
            result = result.stream().filter(g -> g.getFirm().getName()
                    .contains(dto.getFirmName())).collect(Collectors.toList());
        }
        List<GoodsDto> goodsDtos = convertToDtoList(result);
        GoodsDtoContainer container = new GoodsDtoContainer();
        container.setGoodsDtos(goodsDtos);
        container.setFilterDto(dto);

        return container;
    }

    @Override
    public List<GoodsDto> getGoodsFromStore(long storeId) {
        return convertToDtoList(goodsRepository.findNotDeletedByStoreId(storeId));
    }

    @Override
    public List<GoodsDto> getGoodsNotFromStore(long storeId) {
        return convertToDtoList(goodsRepository.findNotDeletedNotFromStore(storeId));
    }

    @Override
    public List<String> getSortingNames(String startWith){
        List<String> list = new ArrayList<>(map.size());
        if(map.containsKey(startWith)) {
            list.add(startWith);
            for(String key : map.keySet()){
                if(!key.equals(startWith)){
                    list.add(key);
                }
            }
        }else {
            list.addAll(map.keySet());
        }

        return list;
    }

    @Override
    public void save(GoodsDto dto) {
        goodsRepository.save(convertToEntity(dto));
    }

    @Override
    public GoodsDto getById(long id) {
        GoodsDto dto;
        Optional<Goods> goods = goodsRepository.findById(id);
        dto = goods.map(GoodsDto::new).orElseGet(GoodsDto::new);
        return dto;
    }

    @Override
    @Transactional
    public void delete(long id) {
        goodsRepository.setDeleted(id, true);
    }

    private Goods convertToEntity(GoodsDto dto){
        if(dto.getGuarantee() < 0){
            throw new IllegalStateException("Guarantee must be a positive number");
        }
        int cost;
        try {
            cost = parseCost(dto.getCost());
            if(cost <= 0){
                throw new IllegalStateException("Cost must be a positive number");
            }
        }catch (NumberFormatException e){
            throw new IllegalStateException(dto.getCost()+" is not a number");
        }
        Firm firm = firmRepository.findByName(dto.getFirmName());
        if(firm == null){
            throw new IllegalStateException("There is no firm with name "+dto.getFirmName());
        }
        Long id = dto.getId() == null || dto.getId() == 0 ? null : dto.getId();

        return new Goods(id, dto.getName(), firm, dto.getModel(),
                dto.getTechnicalCharacteristics(), cost, dto.getGuarantee(), false);
    }

    private List<GoodsDto> convertToDtoList(List<Goods> goods){
        return goods.stream().map(GoodsDto::new).collect(Collectors.toList());
    }

    private void correctFilterDto(GoodsFilterDto dto){
        if(dto.getName() == null){
            dto.setName(EMPTY_STRING);
        }
        if(dto.getFirmName() == null){
            dto.setFirmName(EMPTY_STRING);
        }
        if(dto.getModel() == null){
            dto.setModel(EMPTY_STRING);
        }
        if(dto.getTechnicalCharacteristics() == null){
            dto.setTechnicalCharacteristics(EMPTY_STRING);
        }
        if(dto.getCostFrom() == null){
            dto.setCostFrom(EMPTY_STRING);
        }
        if(dto.getCostTo() == null){
            dto.setCostTo(EMPTY_STRING);
        }
        if(dto.getSortBy() == null){
            dto.setSortBy(EMPTY_STRING);
        }
        if(dto.getGuaranteeFrom() == null || dto.getGuaranteeTo() == null
                || dto.getGuaranteeFrom() < 0 || dto.getGuaranteeTo() <= 0){
            dto.setGuaranteeFrom(0);
            dto.setGuaranteeTo(Integer.MAX_VALUE);
        }
        if(dto.getGuaranteeFrom() != null && dto.getGuaranteeTo() != null
                && dto.getGuaranteeFrom() > dto.getGuaranteeTo()){
            int buf = dto.getGuaranteeFrom();
            dto.setGuaranteeFrom(dto.getGuaranteeTo());
            dto.setGuaranteeTo(buf);
        }
        if(dto.getPageSize() <= 0 || dto.getPageNum() < 0){
            dto.setPageSize(DEFAULT_PAGE_SIZE);
            dto.setPageNum(DEFAULT_PAGE_NUM);
        }
    }

    private int parseCost(String cost){
        if(cost != null && cost.matches("\\d+([.,]\\d{1,2})?")){
            String[] costParts = cost.split("[.,]");
            int result = Integer.parseInt(costParts[0])*100;
            return costParts.length == 1 ? result : result + Integer.parseInt(costParts[1]);
        }

        return 0;
    }
}
