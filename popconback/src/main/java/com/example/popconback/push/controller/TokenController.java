package com.example.popconback.push.controller;

import com.example.popconback.gifticon.domain.Gifticon;
import com.example.popconback.gifticon.dto.GifticonDto;
import com.example.popconback.gifticon.service.GifticonService;
import com.example.popconback.push.service.FirebaseCloudMessageService;
import com.example.popconback.user.domain.User;
import com.example.popconback.user.dto.CreateUser.CreateUserDto;
import com.example.popconback.user.dto.UserDto;
import com.example.popconback.user.repository.UserRepository;
import com.example.popconback.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Api(value = "TokenController")
@SwaggerDefinition(tags = {@Tag(name = "TokenController",
        description = "FCM 토큰 컨트롤러")})
@RequestMapping(value = "/api/v1")
@RestController
@CrossOrigin("*")
public class TokenController {

    private static final Logger logger = LoggerFactory.getLogger(TokenController.class);

    @Autowired
    FirebaseCloudMessageService service;

    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    GifticonService gifticonService;

    @ApiOperation(value = "registToken", notes = "토큰을 받는 Method", httpMethod = "POST")
    @PostMapping("/token")
    public String registToken(String token) {
        logger.info("registToken : token:{}", token);
        service.addToken(token);
        return "'"+token+"'" ;
    }

    @ApiOperation(value = "broadCast", notes = "전체 메세지를 전송하는 Method", httpMethod = "POST")
    @PostMapping("/broadcast")
    public Integer broadCast(String title, String body) throws IOException {
        logger.info("broadCast : title:{}, body:{}", title, body);
        return service.broadCastMessage(title, body);
    }

    // test용
    @PostMapping("/sendMessageTo")
    public void sendMessageTo(String token, String title, String body) throws IOException {
        logger.info("sendMessageTo : token:{}, title:{}, body:{}", token, title, body);
        service.sendMessageTo(token, title, body);
    }
    // test용
    @GetMapping("/push/{hash}")
    public ResponseEntity<List<GifticonDto>> sendMessagePerodic(@PathVariable int hash){
        //List<User> U_list = userService.getAllUser();
        //List<Gifticon> G_list;

        Optional<User> user = userRepository.findById(hash);
        User nuser = user.get();
        int Dday = nuser.getNday();
        return ResponseEntity.ok(gifticonService.getPushGifticon(hash, Dday));
    }


    public void pushmessage(int timezone) throws IOException{
        List<UserDto> U_list = userService.getAllUser();
        for (UserDto user : U_list) {
            if(user.getAlarm() == 0 || user.getTimezone() != timezone) {// 알람 설정 안한 사람은 스킵 시간대 아니면 스킵 아침 0 점심 1 저녁 2
                continue;
            }

            int hash = user.hashCode();
            int Dday = user.getNday();
            String Token = user.getToken();
            List<GifticonDto> list = gifticonService.getPushGifticon(hash, Dday);// 설정한 알림 기간에 해당하는 기프티콘 리스트


            for (GifticonDto gifticon : list) {
                Date date = java.sql.Date.valueOf(LocalDate.now().plusDays(Dday));//오늘 날짜에 알람설정 일 수를 더하고
                Date Ddate = gifticon.getDue();// 사용 기간을 구하고
                long diffsec = (date.getTime() - Ddate.getTime())/1000+(60*60*9l);// 둘의 차이를 뺀다
                long diffday = diffsec/(24*60*60);// 얼만큼 지나갔는지 확인하기 위해서 일 수를 구하고
                if(diffday%user.getTerm() == 0){// 해당 일수를 알람 주기로 나눠서 나머지가 0이면 해당되는 날에 알림을 보낸다.
                    service.sendMessageTo(Token, "쿠폰 유효기간 알림", "유효기간이 임박한 기프티콘 있어요!");
                    System.out.println("hihi");
                    break;// 문자 여러개 보낼 필요 없으니까
                }
            }
//
        }

    }
    @Scheduled(cron="0 0 09 * * ?")
    @GetMapping("/push/pushtest/")
    public void morning_pushmessage() throws IOException {
        pushmessage(0);
    }

    @Scheduled(cron="0 0 13 * * ?")
    public void noon_pushmessage() throws IOException {
        pushmessage(1);
    }

    @Scheduled(cron="0 0 18 * * ?")
    public void even_pushmessage() throws IOException {
        pushmessage(2);
    }

    @Scheduled(cron="0 0 22 * * ?")
    public void crontest() throws IOException {
        System.out.println("tetetete");
    }

}
