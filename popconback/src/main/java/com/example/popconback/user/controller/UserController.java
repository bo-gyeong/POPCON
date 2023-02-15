package com.example.popconback.user.controller;

import com.example.popconback.user.dto.CreateUser.CreateUserDto;
import com.example.popconback.user.dto.CreateUser.ResponsCreateUserDto;
import com.example.popconback.user.dto.DeleteUser.DeleteUserDto;
import com.example.popconback.user.dto.Token.ResponseToken;
import com.example.popconback.user.dto.UpdateUser.ResponseUpdateUserDto;
import com.example.popconback.user.dto.UpdateUser.UpdateUserDto;
import com.example.popconback.user.dto.UserDto;
import com.example.popconback.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Api(value = "UserController")
@SwaggerDefinition(tags = {@Tag(name = "UserController",
        description = "유저 컨트롤러")})
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/user")
public class UserController {

    private final UserService userservice;

    private boolean refreshFlag = true;


    @ApiOperation(value = "login",
            notes = "회원 정보 DB에 저장",
            httpMethod = "POST")
    @PostMapping("/login") // 로그인
    public ResponseEntity<ResponsCreateUserDto> login(@RequestBody CreateUserDto createUserDto){
        return ResponseEntity.ok(userservice.login(createUserDto));// 카카오 토큰을 받아와서 사용자 정부 추출
    }

    @ApiOperation(value = "refresh",
            notes = "리프레시토큰발급",
            httpMethod = "GET")
    @GetMapping("/refresh") // 리프레시하기
    public ResponseEntity<ResponseToken> refresh(HttpServletRequest request){// 필터에서 안걸러지면 유효기간 남아있는거임
        if (refreshFlag){
            refreshFlag = false;
            String token = request.getHeader(HttpHeaders.AUTHORIZATION).split(" ")[1];
            return ResponseEntity.ok(userservice.refresh(token));
        }else{
            return ResponseEntity.ok().build();
        }
    }
    @Scheduled(cron = "0/5 * * * * *")
    public void Flag_reset () {
        refreshFlag = true;
    }


//    @ApiOperation(value = "createUserK",
//            notes = "회원 정보 DB에 저장(카카오)",
//            httpMethod = "POST")
//    @PostMapping("/login/kakao") // 회원 정보 DB에 저장(카카오)
//    public ResponseEntity<ResponsCreateUserDto> createUserK(@RequestBody CreateUserDto createUserDto){
//        return ResponseEntity.ok(userservice.CreateUser(createUserDto));
//    }
//
//    @ApiOperation(value = "createUserN",
//            notes = "회원 정보 DB에 저장(네이버)",
//            httpMethod = "POST")
//    @PostMapping("/login/naver") // 회원 정보 DB에 저장(네이버)
//    public ResponseEntity<ResponsCreateUserDto> createUserN(@RequestBody CreateUserDto createUserDto){
//        return ResponseEntity.ok(userservice.CreateUser(createUserDto));
//    }

    @ApiOperation(value = "updateUser",
            notes = "회원 정보 수정",
            httpMethod = "POST")
    @PostMapping("/update")// 회원 정보 수정
    public ResponseEntity<ResponseUpdateUserDto> updateUser(@RequestBody UpdateUserDto updateUserDto, Authentication authentication){
        UserDto us= (UserDto)authentication.getPrincipal();
        return ResponseEntity.ok(userservice.updateUser(updateUserDto,us.hashCode()));
    }

    @ApiOperation(value = "deleteUser",
            notes = "회원 탈퇴",
            httpMethod = "DELETE")
    @DeleteMapping("/withdrawal") //회원 탈퇴
    public ResponseEntity<Void> deleteUser(Authentication authentication){
        UserDto us= (UserDto)authentication.getPrincipal();
        userservice.deleteUser(us.hashCode());
        return ResponseEntity.ok().build();
    }
    @GetMapping("/getlevel")
    public ResponseEntity<Integer> getUserlv(Authentication authentication){
        UserDto us= (UserDto)authentication.getPrincipal();
        return ResponseEntity.ok(userservice.getLevel(us.hashCode()));
    }

    //@GET("/notification") 푸시 알림 정보

}
