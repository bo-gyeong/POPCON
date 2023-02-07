package com.example.popconback.gifticon.dto.OCR;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class CheckImageSizeDto {

    int width;
    int height;

    public CheckImageSizeDto() {}

    @Builder
    public CheckImageSizeDto(int width, int height) {
        this.width = width;
        this.height = height;
    }
}
