package com.example.popconback.gifticon.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ResponseBrandDto {
    @ApiModelProperty(name = "brandName", value = "브랜드 명", example = "스타벅스")
    private String brandName;
    @ApiModelProperty(name = "brandImg", value = "브랜드 로고 URL", example = "https://contents.lotteon.com/search/brand/P2/38/6/P2386_320_320.jpg/dims/optimize/dims/resize/400x400")
    private String brandImg;

    public ResponseBrandDto() {}
}
