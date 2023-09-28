package org.store.dto;

import org.store.domain.OnlineStore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class OnlineStoreDto {
    private Long id;

    private String internetAddress;

    private String deliveryPaid;

    public OnlineStoreDto(OnlineStore store) {
        id = store.getId();
        internetAddress = store.getInternetAddress();
        deliveryPaid = store.getDeliveryPaid() ? "Yes" : "No";
    }
}
