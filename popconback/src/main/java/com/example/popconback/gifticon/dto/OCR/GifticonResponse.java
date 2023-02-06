package com.example.popconback.gifticon.dto.OCR;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
public class GifticonResponse {
    // 발행처, 브랜드, 상품명, 상품좌표, 사용기한, 바코드번호, 바코드좌표

    @ApiModelProperty(name = "isVoucher", value = "0:금액권x / 1:금액권o", example = "0 or 1")
    private int isVoucher;

    @ApiModelProperty(name = "price", value = "금액권 가격", example = "3000 or -1 (인식안됐을때)")
    private int price;

    @ApiModelProperty(name = "publisher", value = "발행처", example = "GS&쿠폰")
    private String publisher;
    @ApiModelProperty(name = "brand", value = "브랜드명", example = "스타벅스")
    private String brandName;
    @ApiModelProperty(name = "productName", value = "상품명", example = "아이스 카페 아메리카노 Tall")
    private String productName;

    private Map<String, String> productImg = new HashMap<>();

    private Map<String, String> due;

    @ApiModelProperty(name = "barcodeNum", value = "바코드 넘버", example = "1234-5678-9999")
    private String barcodeNum;

    private Map<String, String> barcodeImg = new HashMap<>();

    private int validation;  // 0: 정상 , 1: 바코드 중복 , 2: 브랜드 없음, 3: 1&2 , -1: 형식에 맞지않는 기프티콘


    public GifticonResponse() {}

    public GifticonResponse(int isVoucher, int price, String publisher, String brandName, String productName, Map<String, String> productImg, Map<String, String> due, String barcodeNum,Map<String, String> barcodeImg, int validation){
        this.isVoucher = isVoucher;
        this.price = price;
        this.publisher = publisher;
        this.brandName = brandName;
        this.productName = productName;
        this.productImg = productImg;
        this.due = due;
        this.barcodeNum = barcodeNum;
        this.barcodeImg = barcodeImg;
        this.validation = validation;
    }






}
