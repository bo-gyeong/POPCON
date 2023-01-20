package com.example.popconback.gifticon.dto.CreateGifticon;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class ResponseCreateGifticonDto {

    private String barcodeNum;
    private int hash;
    private String brandName;
    private String product;
    @JsonFormat( shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date due;
    private int price;
    private int state;
    private String memo;
}
