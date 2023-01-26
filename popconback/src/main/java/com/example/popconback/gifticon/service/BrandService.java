package com.example.popconback.gifticon.service;

import com.example.popconback.gifticon.domain.Brand;

import com.example.popconback.gifticon.dto.CreateGifticon.ResponseCreateGifticonDto;
import com.example.popconback.gifticon.dto.ResponseBrandDto;
import com.example.popconback.gifticon.repository.Brandrepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class BrandService {

    private final Brandrepository brandrepository;

    public ResponseBrandDto findBrand (String brandName){
        Brand brandInfo = brandrepository.findByBrandName(brandName);

        ResponseBrandDto responseBrandDto = new ResponseBrandDto();

        responseBrandDto.setBrandName(brandInfo.getBrandName());
        responseBrandDto.setBrandImg(brandInfo.getBrandImg());

        return responseBrandDto;

    }



}
