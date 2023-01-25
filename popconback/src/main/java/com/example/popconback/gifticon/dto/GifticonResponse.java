package com.example.popconback.gifticon.dto;

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
    @ApiModelProperty(name = "publisher", value = "발행처", example = "GS&쿠폰")
    private String publisher;
    @ApiModelProperty(name = "brand", value = "브랜드명", example = "스타벅스")
    private String brand;
    @ApiModelProperty(name = "productName", value = "상품명", example = "아이스 카페 아메리카노 Tall")
    private String productName;

    private Map<String, String> productImg = new HashMap<>();

    private Map<String, String> expiration;

    @ApiModelProperty(name = "barcodeNum", value = "바코드 넘버", example = "1234-5678-9999")
    private String barcodeNum;

    private Map<String, String> barcodeImg = new HashMap<>();

    public GifticonResponse() {}

    public GifticonResponse(String publisher, String brand, String productName, Map<String, String> productImg, Map<String, String> expiration, String barcodeNum,Map<String, String> barcodeImg ){
        this.publisher = publisher;
        this.brand = brand;
        this.productName = productName;
        this.productImg = productImg;
        this.expiration = expiration;
        this.barcodeNum = barcodeNum;
        this.barcodeImg = barcodeImg;
    }






}
