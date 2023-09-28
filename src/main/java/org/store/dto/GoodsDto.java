package org.store.dto;

import org.store.domain.Goods;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class GoodsDto {
    private Long id;
    private String name;
    private String firmName;
    private String model;
    private String technicalCharacteristics;
    private String cost;
    private int guarantee;

    public GoodsDto(Goods goods){
        id = goods.getId();
        name = goods.getName();
        firmName = goods.getFirm().getName();
        model = goods.getModel();
        cost = Integer.toString(goods.getCost());
        cost = cost.substring(0, cost.length()-2)+'.'+cost.substring(cost.length()-2);
        guarantee = goods.getGuaranteeTerm();
        technicalCharacteristics = goods.getTechnicalCharacteristics();
    }
}
