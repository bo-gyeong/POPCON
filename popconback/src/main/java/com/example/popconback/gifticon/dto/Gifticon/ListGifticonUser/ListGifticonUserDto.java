package com.example.popconback.gifticon.dto.Gifticon.ListGifticonUser;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ListGifticonUserDto {

    @ApiModelProperty(name = "brandName", value = "브랜드명", example = "스타벅스")
    private String brandName;

    @ApiModelProperty(name = "email", value = "유저 계정 이메일", example = "abc@naver.com")
    private String email;
    @ApiModelProperty(name = "social", value = "소셜 로그인 구분, ex)'카카오', '네이버'", example = "카카오")
    private String social;

}
