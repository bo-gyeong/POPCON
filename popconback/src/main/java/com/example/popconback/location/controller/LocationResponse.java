package com.example.popconback.location.controller;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;



@Getter
@Setter
@ToString
public class LocationResponse {
    // phone, place_name, x, y
    @ApiModelProperty(name = "phone", value = "매장 전화번호", example = "1522-3232")
    private String phone;
    @ApiModelProperty(name = "placeName", value = "매장명", example = "스타벅스 구미인동점")
    private String placeName;
    @ApiModelProperty(name = "xPos", value = "위도", example = "128.4176")
    private String xPos;
    @ApiModelProperty(name = "yPos", value = "경도", example = "36.1079")
    private String yPos;

    @ApiModelProperty(name = "brandName", value = "브랜드명", example = "스타벅스")
    private String brandName;


    public LocationResponse() {}

    public LocationResponse(String phone, String placeName, String xPos, String yPos, String brandName){
        this.phone = phone;
        this.placeName = placeName;
        this.xPos = xPos;
        this.yPos = yPos;
        this.brandName = brandName;
    }






}

