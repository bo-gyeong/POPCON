package com.example.popconback.gifticon.service;

import com.example.popconback.gifticon.domain.Present;
import com.example.popconback.gifticon.dto.Present.ResponsePossiblePresentListDto;
import com.example.popconback.gifticon.repository.PresentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor
public class PresentService {

    private final PresentRepository presentRepository;

    private double deg2rad(double deg){
        return (deg * Math.PI/180.0);
    }
    //radian(라디안)을 10진수로 변환
    private double rad2deg(double rad){
        return (rad * 180 / Math.PI);
    }

    public double getDistance(double lon1, double lat1, double lon2, double lat2){
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))* Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1))*Math.cos(deg2rad(lat2))*Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60*1.1515*1609.344;

        return dist; //단위 meter
    }


    public ResponsePossiblePresentListDto findPresentByPosition(String x, String y) {

        double nowX = Double.parseDouble(x);
        double nowY = Double.parseDouble(y);

        List<Present> allPresentList = presentRepository.findAll();

        List<String> allNearPresentList = new ArrayList<>();

        List<String> gettablePresentList = new ArrayList<>();

        for (Present present : allPresentList) {
            String barcodeNum = present.getGifticon().getBarcodeNum();

            double xPos = Double.parseDouble(present.getX());
            double yPos = Double.parseDouble(present.getY());

            if (getDistance(nowX, nowY, xPos , yPos)<=2000 && getDistance(nowX, nowY, xPos , yPos)>30) {
                allNearPresentList.add(barcodeNum);
            }
            else if (getDistance(nowX, nowY, xPos , yPos)<=30) {
                gettablePresentList.add(barcodeNum);
            }

        }

        ResponsePossiblePresentListDto responsePossiblePresentDto = new ResponsePossiblePresentListDto();

        responsePossiblePresentDto.setAllNearPresentList(allNearPresentList);
        responsePossiblePresentDto.setGettablePresentList(gettablePresentList);


        return responsePossiblePresentDto;

    }
}
