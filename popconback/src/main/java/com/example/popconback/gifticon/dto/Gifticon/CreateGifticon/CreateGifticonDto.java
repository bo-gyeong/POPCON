package com.example.popconback.gifticon.dto.Gifticon.CreateGifticon;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;

import java.util.Date;

@Data
public class CreateGifticonDto {

    @ApiModelProperty(name = "email", value = "유저 계정 이메일", example = "abc@naver.com")
    private String email;
    @ApiModelProperty(name = "social", value = "소셜 로그인 구분, ex)'카카오', '네이버'", example = "카카오")
    private String social;
    @ApiModelProperty(name = "barcodeNum", value = "바코드 넘버", example = "1234-5678-9999")
    private String barcodeNum;
    @ApiModelProperty(name = "brandName", value = "브랜드명", example = "스타벅스")
    private String brandName;
    @ApiModelProperty(name = "productName", value = "상품명", example = "아이스 카페 아메리카노 Tall")
    private String productName;
    @ApiModelProperty(name = "due", value = "유효기간", example = "2023-01-10 00:00:00.000000")
    private Date due;
    @ApiModelProperty(name = "price", value = "int:금액권의 잔여금액, -1:교환권", example = "-1")
    private int price;
    @ApiModelProperty(name = "state", value = "0:사용가능, 1:사용완료, 2:기간만료", example = "2")
    private int state;
    @ApiModelProperty(name = "memo", value = "유저 메모", example = "유라 우수 참여")
    private String memo;
    @ApiModelProperty(name = "isVoucher", value = "0:그냥 기프티콘, 1:금액권", example = "1")
    private int  isVoucher;
}
