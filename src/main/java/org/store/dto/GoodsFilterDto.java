package org.store.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsFilterDto implements Serializable {
    private String name;
    private String firmName;
    private String model;
    private String technicalCharacteristics;
    private String costFrom;
    private String costTo;
    private Integer guaranteeFrom;
    private Integer guaranteeTo;
    private String sortBy;
    private int pageNum;
    private int pageSize;
}
