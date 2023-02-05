package com.example.popconback.gifticon.dto.Gifticon.ListGifticonUser;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BrandForRLGUDto {
    @ApiModelProperty(name = "brandName", value = "브랜드명", example = "스타벅스")
    private String brandName;
    @ApiModelProperty(name = "brandImg", value = "브랜드이미지")
    private String brandImg;

}
