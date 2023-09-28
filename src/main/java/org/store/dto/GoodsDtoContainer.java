package org.store.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsDtoContainer {
    private List<GoodsDto> goodsDtos;

    private GoodsFilterDto filterDto;
}
