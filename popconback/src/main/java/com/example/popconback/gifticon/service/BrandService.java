package com.example.popconback.gifticon.service;

import com.example.popconback.gifticon.domain.Brand;

import com.example.popconback.gifticon.dto.ResponseBrandDto;
import com.example.popconback.gifticon.repository.Brandrepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


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

    public int checkBrand (String brandName) {

        int checked = 0;

        List<Brand> allBrandList = brandrepository.findAll();
        List<String> allBrandNames = new ArrayList<>();
        try {
            for (Brand brand : allBrandList) {
                String nowBrand = brand.getBrandName();
                allBrandNames.add(nowBrand);

            }

            for (String now : allBrandNames) {
                if (brandName.contains(now)) {
                    checked = 1;
                }
            }
        }
        catch (NullPointerException e) {
            System.out.println(e);
        }


        return checked;
    }

    public List<ResponseBrandDto> brandListOrderByGifticonCount (){
        List<Brand> brandList = brandrepository.findAllByOrderByCountOfGifticonsDesc();

        List<ResponseBrandDto> resultList = new ArrayList<>();

        for (Brand brand : brandList) {
            ResponseBrandDto responseBrandDto = new ResponseBrandDto();

            int nowGifticonCount = brand.getCountOfGifticons();

            if (nowGifticonCount > 0) {
                responseBrandDto.setBrandName(brand.getBrandName());
                responseBrandDto.setBrandImg(brand.getBrandImg());

                resultList.add(responseBrandDto);
            }

        }


        return resultList;

    }





}
