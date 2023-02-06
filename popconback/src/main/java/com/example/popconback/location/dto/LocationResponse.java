package com.example.popconback.location.dto;

import com.example.popconback.gifticon.dto.ResponseBrandDto;
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

    @ApiModelProperty(name = "brandInfo", value = "{브랜드명, 브랜드 로고 URL}", example = "{brandName:스타벅스,brandImg:https://contents.lotteon.com/search/brand/P2/38/6/P2386_320_320.jpg/dims/optimize/dims/resize/400x400}")
    private ResponseBrandDto brandInfo;


    public LocationResponse() {}

    public LocationResponse(String phone, String placeName, String xPos, String yPos, ResponseBrandDto brandInfo){
        this.phone = phone;
        this.placeName = placeName;
        this.xPos = xPos;
        this.yPos = yPos;
        this.brandInfo = brandInfo;
    }






}

