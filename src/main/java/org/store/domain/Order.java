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
@FieldDefaults(level= AccessLevel.PRIVATE)
@Table(name = "orders", schema = "online_sales")
@Entity
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @OneToOne
    @JoinColumn(name = "market_id")
    OnlineStore onlineStore;
    @OneToOne
    @JoinColumn(name = "goods_id")
    Goods goods;
    @Column(name = "order_date_time")
    LocalDateTime orderDateTime;
    @Column
    Integer amount;
    @Column(name = "client_full_name")
    String clientFullName;
    @Column
    String telephone;
    @Column(name = "order_confirmation")
    Boolean orderConfirmation;
    @Column
    boolean deleted;
}
