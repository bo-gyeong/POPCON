package com.example.popconback.gifticon.dto.Gifticon.CreateGifticon;

import io.swagger.annotations.ApiModelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class ResponseCreateGifticonDto {

    @ApiModelProperty(name = "barcodeNum", value = "바코드 넘버", example = "1234-5678-9999")
    private String barcodeNum;
    @ApiModelProperty(name = "brandName", value = "브랜드명", example = "스타벅스")
    private String brandName;
    @ApiModelProperty(name = "productName", value = "상품명", example = "아이스 카페 아메리카노 Tall")
    private String productName;
    @ApiModelProperty(name = "due", value = "유효기간", example = "2023-01-10 00:00:00.000000")
    @JsonFormat( shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
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
