package org.store.service.impl;

import org.store.dao.FirmRepository;
import org.store.dao.GoodsRepository;
import org.store.domain.Firm;
import org.store.service.FirmService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FirmServiceImpl implements FirmService {

    private final FirmRepository firmRepository;

    private final GoodsRepository goodsRepository;

    @Override
    public Map<Long, String> getAll() {
        return firmRepository.findByDeleted(false).stream().collect(Collectors.toMap(Firm::getId, Firm::getName));
    }

    @Override
    public String getFirmById(long id) {
        Optional<Firm> firm = firmRepository.findById(id);

        return firm.isPresent() ? firm.get().getName() : "";
    }

    @Override
    public boolean create(String name){
        if(firmRepository.exists(Example.of(new Firm(null, name, false)))){
            return false;
        }
        Optional<Firm> deletedFirm = firmRepository.findOne(Example.of(new Firm(null, name, true)));
        if(deletedFirm.isPresent()){
            Long id = deletedFirm.get().getId();
            firmRepository.setDeleted(id, false);
        }else {
            firmRepository.save(new Firm(null, name, false));
        }
        return true;
    }

    @Override
    public boolean updateFirm(long id, String name) {
        Firm firm = firmRepository.findByName(name);
        if(firm != null){
            return firm.getId().equals(id);
        }
        firmRepository.save(new Firm(id, name, false));

        return true;
    }

    @Override
    public boolean deleteFirm(long id, String name) {
        if(goodsRepository.existsByFirmAndDeleted(new Firm(id, name, false), false)){
            return false;
        }
        firmRepository.setDeleted(id, true);
        return true;
    }
}
