package com.example.popconback.gifticon.dto.UpdateGifticon;

import lombok.Data;

import java.util.Date;

@Data
public class ResponseUpdateGifticonDto {


    private String barcode_num;
    private int hash;
    private String brandName;
    private String product;
    private Date due;
    private int price;
    private int state;
    private String memo;
}
