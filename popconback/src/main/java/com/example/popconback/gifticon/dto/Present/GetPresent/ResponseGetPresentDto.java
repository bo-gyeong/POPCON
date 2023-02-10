package com.example.popconback.gifticon.dto.Present.GetPresent;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ResponseGetPresentDto {
    @ApiModelProperty(name = "barcodeNum", value = "바코드넘버", example = "12345678")
    private String barcodeNum;

}
