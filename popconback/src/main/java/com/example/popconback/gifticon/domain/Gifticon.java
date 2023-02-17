package com.example.popconback.gifticon.domain;

import com.example.popconback.files.domain.InputFile;
import com.example.popconback.user.domain.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name="gifticon")
public class Gifticon {

    @Id
    @Column(name="barcode_num")
    private String barcodeNum;

    @ManyToOne
    @JoinColumn(name="hash")
    @JsonManagedReference
    private User user;

    @ManyToOne
    @JoinColumn(name="brand_name")
    @JsonManagedReference
    private Brand brand;
    private String productName;
    private Date due;
    private int price;
    @JsonManagedReference
    @OneToMany(mappedBy = "gifticon", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<InputFile> filesList = new ArrayList<>();



    private int state;
    private String memo;


    private int isVoucher;

    @OneToOne(mappedBy = "gifticon", cascade = CascadeType.REMOVE)// 여기가 문제 였음
    private Present present;

}
