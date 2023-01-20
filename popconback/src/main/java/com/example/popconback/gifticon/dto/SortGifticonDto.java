package com.example.popconback.gifticon.dto;

import lombok.Data;

@Data
public class SortGifticonDto {

    private int hash;
    private String brandName; // 1:유효기간 적게 남은 순

    private String email;
    private String social;

}
