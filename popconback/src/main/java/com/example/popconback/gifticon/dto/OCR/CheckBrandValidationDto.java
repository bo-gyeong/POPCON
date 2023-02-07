package com.example.popconback.gifticon.dto.OCR;

import lombok.Builder;
import lombok.Data;

@Data
public class CheckBrandValidationDto {

    int result;  // 0: Error , 1: Success

    String rightBrand;  // ex) CU

    @Builder
    public CheckBrandValidationDto(int result, String rightBrand) {

        this.result = result;
        this.rightBrand = rightBrand;
    }


}
