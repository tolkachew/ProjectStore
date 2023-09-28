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
@FieldDefaults(level= AccessLevel.PRIVATE)
@Table(name = "goods", schema = "online_sales")
@Entity
public class Goods {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column
    String name;
    @OneToOne
    @JoinColumn(name = "firm_id")
    Firm firm;
    @Column
    String model;
    @Column(name = "tech_characteristics")
    String technicalCharacteristics;
    @Column
    int cost;
    @Column(name = "guarantee_term")
    int guaranteeTerm;
    @Column
    boolean deleted;
}
