package com.example.popconback.gifticon.service;

import com.example.popconback.gifticon.domain.Gifticon;
import com.example.popconback.gifticon.domain.Present;
import com.example.popconback.gifticon.dto.GifticonDto;
import com.example.popconback.gifticon.dto.Present.GetPresent.GetPresentDto;
import com.example.popconback.gifticon.dto.Present.GetPresent.ResponseGetPresentDto;
import com.example.popconback.gifticon.dto.Present.GivePresent.GivePresentDto;
import com.example.popconback.gifticon.dto.Present.GivePresent.ResponseGivePresentDto;
import com.example.popconback.gifticon.dto.Present.PossiblePresentList.ResponsePossiblePresentDto;
import com.example.popconback.gifticon.dto.Present.PossiblePresentList.ResponsePossiblePresentListDto;
import com.example.popconback.gifticon.repository.GifticonRepository;
import com.example.popconback.gifticon.repository.PresentRepository;
import com.example.popconback.push.controller.TokenController;
import com.example.popconback.user.domain.User;
import com.example.popconback.user.dto.UserDto;
import com.example.popconback.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PresentService {

    private final PresentRepository presentRepository;
    private final GifticonRepository gifticonRepository;
    private final UserRepository userRepository;

    private final TokenController tokenController;




    public ResponseGivePresentDto givePresent(GivePresentDto givePresentDto) {
        String presentBarcode = givePresentDto.getBarcodeNum();
        String presentX = givePresentDto.getX();
        String presentY = givePresentDto.getY();


        Optional<Gifticon> optionalGifticon = gifticonRepository.findById(presentBarcode);
        if(!optionalGifticon.isPresent()){
            return null;
        }
        Gifticon gifticon = optionalGifticon.get();
        gifticon.setState(3);

        Present present = new Present();

        present.setGifticon(gifticon);
        present.setX(presentX);
        present.setY(presentY);

        presentRepository.save(present);


        return null;


    }


    public ResponseGetPresentDto getPresent(GetPresentDto getPresentDto, int hash) {

        // 기프티콘 상태 바꾸기
        GifticonDto Present_gifticon = new GifticonDto();
        Optional<Gifticon> optionalGifticon = gifticonRepository.findById(getPresentDto.getBarcodeNum());
        if(!optionalGifticon.isPresent()){
            return null;
        }
        Gifticon gifticon = optionalGifticon.get();

        User donationUser = gifticon.getUser();
        int updatedTemp = donationUser.getManner_temp()+1;
        UserDto tuser = new UserDto();
        BeanUtils.copyProperties(donationUser,tuser);
        tuser.setManner_temp(updatedTemp);
        userRepository.save(tuser.toEntity());// 온도 올려주고



        // 문자보내기

//        try {
//            if(updatedTemp%3 == 0){
//                int level = (updatedTemp/3+1);
//                tokenController.sendMessageTo(gifticon.getUser().getToken(), "level"+level+"달성을 축하드립니다.", getPresentDto.getMessage());
//            }
//            if(updatedTemp == 1){
//                int level = 1;
//                tokenController.sendMessageTo(gifticon.getUser().getToken(), "level"+level+"달성을 축하드립니다.", getPresentDto.getMessage());
//            }
//        }catch(IOException e){
//
//        }

        try {
            String title = "선물이 누군가에게 전달되었어요. 감사인사를 확인해보세요!";
            tokenController.sendMessageTo(gifticon.getUser().getToken(), title, getPresentDto.getMessage());
            System.out.println("감사합니다"+getPresentDto.getMessage());
        }
        catch (IOException e){

        }

        gifticon.setUser(userRepository.findById(hash).get());
        gifticon.setState(0);
        gifticonRepository.save(gifticon);
        // 선물테이블에서 지우기
        presentRepository.deleteByGifticon_BarcodeNum(getPresentDto.getBarcodeNum());

        ResponseGetPresentDto responseGetPresentDto = new ResponseGetPresentDto();
        responseGetPresentDto.setBarcodeNum(gifticon.getBarcodeNum());

        return responseGetPresentDto;
    }


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


    public ResponsePossiblePresentListDto findPresentByPosition(String x, String y, int mannerTemp) {
        // 매너온도별 줍기가능 구간
        //0, 1, 3,6,9,12개 (주워야 확장!)
        //0, 20/ 100 150 300 400
        //30, 50, 150, 300, 600, 1000

        int possibleDist = 30;

        if (mannerTemp >= 1 && mannerTemp < 3) {
            possibleDist = 50;
        } else if (mannerTemp >= 3 && mannerTemp < 6) {
            possibleDist = 150;
        } else if (mannerTemp >= 6 && mannerTemp < 9) {
            possibleDist = 300;
        } else if (mannerTemp >= 9 && mannerTemp < 12) {
            possibleDist = 600;
        } else if (mannerTemp >= 12) {
            possibleDist = 1000;
        }


        double nowX = Double.parseDouble(x);
        double nowY = Double.parseDouble(y);

        List<Present> allPresentList = presentRepository.findAll();

        List<ResponsePossiblePresentDto> allNearPresentList = new ArrayList<>();

        List<ResponsePossiblePresentDto> gettablePresentList = new ArrayList<>();

        for (Present present : allPresentList) {
            String barcodeNum = present.getGifticon().getBarcodeNum();

            double xPos = Double.parseDouble(present.getX());
            double yPos = Double.parseDouble(present.getY());



            if (getDistance(nowX, nowY, xPos , yPos)<=2000 && getDistance(nowX, nowY, xPos , yPos)>possibleDist) {
                ResponsePossiblePresentDto responsePossiblePresentDto = new ResponsePossiblePresentDto();
                responsePossiblePresentDto.setBarcodeNum(barcodeNum);
                responsePossiblePresentDto.setX(present.getX());
                responsePossiblePresentDto.setY(present.getY());
                allNearPresentList.add(responsePossiblePresentDto);
            }
            else if (getDistance(nowX, nowY, xPos , yPos)<=possibleDist) {
                ResponsePossiblePresentDto responsePossiblePresentDto = new ResponsePossiblePresentDto();
                responsePossiblePresentDto.setBarcodeNum(barcodeNum);
                responsePossiblePresentDto.setX(present.getX());
                responsePossiblePresentDto.setY(present.getY());
                gettablePresentList.add(responsePossiblePresentDto);
            }

        }

        ResponsePossiblePresentListDto responsePossiblePresentDto = new ResponsePossiblePresentListDto();

        responsePossiblePresentDto.setAllNearPresentList(allNearPresentList);
        responsePossiblePresentDto.setGettablePresentList(gettablePresentList);


        return responsePossiblePresentDto;

    }
}
