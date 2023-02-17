package com.example.popconback.gifticon.domain;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Getter
@Setter
@Table(name="present")
public class Present {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "present_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "barcode_num")
    private Gifticon gifticon;

    private String x;
    private String y;



}
