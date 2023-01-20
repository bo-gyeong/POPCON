package com.example.popconback.gifticon.dto.UpdateGifticon;

import lombok.Data;

import java.util.Date;

@Data
public class UpdateGifticonDto {


    private String barcodeNum;
    private int hash;
    private String brandName;
    private String product;
    private Date due;
    private int price;
    private int state;
    private String memo;
}
