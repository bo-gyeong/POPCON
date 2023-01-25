package com.example.popconback.gifticon.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SortGifticonDto {

    @ApiModelProperty(name = "hash", value = "유저 hash값", example = "1305943263")
    private int hash;
    private String brandName; // 1:유효기간 적게 남은 순

    @ApiModelProperty(name = "email", value = "유저 계정 이메일", example = "abc@naver.com")
    private String email;
    @ApiModelProperty(name = "social", value = "소셜 로그인 구분, ex)'카카오', '네이버'", example = "카카오")
    private String social;

}
