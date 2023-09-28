package org.store.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "delivery", schema = "online_sales")
@Entity
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @OneToOne
    @JoinColumn(name = "order_id")
    Order order;
    @Column(name = "delivery_date_time")
    LocalDateTime deliveryDateTime;
    @Column
    String address;
    @Column(name = "courier_full_name")
    String courierFullName;
    @Column(name = "delivery_cost")
    int deliveryCost;
    @Column
    boolean deleted;
}
