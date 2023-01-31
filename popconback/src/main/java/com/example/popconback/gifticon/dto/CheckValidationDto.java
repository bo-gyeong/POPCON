package com.example.popconback.gifticon.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class CheckValidationDto {

    int result;  // 0: Error , 1: Success

    @Builder
    public CheckValidationDto(int result) {
        this.result = result;
    }

}
