package org.store.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OnlineStoreFilterDto {
    private String internetAddress;
    private String payment;
    private String sortBy;
    private int pageNum;
    private int pageSize;


}
