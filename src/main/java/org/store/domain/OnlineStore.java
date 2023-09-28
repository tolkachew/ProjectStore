package org.store.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "internet_markets", schema = "online_sales")
@Entity
public class OnlineStore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "internet_address")
    String internetAddress;
    @Column(name = "is_delivery_paid")
    Boolean deliveryPaid;
    @Column
    boolean deleted;
}
