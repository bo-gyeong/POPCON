package com.example.popconback.gifticon.dto;

import lombok.Data;

import java.util.Date;

@Data
public class CreateGifticonDto {

    private String barcodeNum;
    private int hash;
    private String brandName;
    private String product;
    private Date due;
    private int price;
    private int state;
    private String memo;

}
