package com.example.popconback.gifticon.dto.OCR;

import lombok.Builder;
import lombok.Data;

@Data
public class CheckBarcodeValidationDto {

    int result;  // 0: Error , 1: Success



    @Builder
    public CheckBarcodeValidationDto(int result) {

        this.result = result;

    }


}
