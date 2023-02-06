package com.example.popconback.gifticon.dto.Present.GetPresent;

import com.example.popconback.gifticon.domain.Gifticon;
import lombok.Data;

@Data
public class GetPresentDto {


    private String barcode_num;

    private String message;
    private String x;
    private String y;


}