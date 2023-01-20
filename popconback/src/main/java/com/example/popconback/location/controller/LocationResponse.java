package com.example.popconback.location.controller;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;



@Getter
@Setter
@ToString
public class LocationResponse {
    // phone, place_name, x, y
    private String phone;
    private String placeName;
    private String xPos;
    private String yPos;

    private String brand;


    public LocationResponse() {}

    public LocationResponse(String phone, String placeName, String xPos, String yPos, String brand){
        this.phone = phone;
        this.placeName = placeName;
        this.xPos = xPos;
        this.yPos = yPos;
        this.brand = brand;

    }






}

