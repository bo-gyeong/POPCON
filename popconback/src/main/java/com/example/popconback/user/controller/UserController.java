package com.example.popconback.user.controller;

import com.example.popconback.user.domain.User;
import com.example.popconback.user.dto.CreateUserDto;
import com.example.popconback.user.dto.DeleteUserDto;
import com.example.popconback.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api")
public class UserController {

    private final UserService userservice;

    @PostMapping("/user/kakao-login") // 회원 정보 DB에 저장(카카오)
    public ResponseEntity<User> createUserK(@RequestBody CreateUserDto createUserDto){
        System.out.println(createUserDto.getEmail());
        System.out.println(createUserDto.getSocial());
        return ResponseEntity.ok(userservice.CreateUser(createUserDto));
    }
    @PostMapping("/user/naver-login") // 회원 정보 DB에 저장(네이버)
    public ResponseEntity<User> createUserN(@RequestBody CreateUserDto createUserDto){
        return ResponseEntity.ok(userservice.CreateUser(createUserDto));
    }
    @DeleteMapping("/user/withdrawal") //회원 탈퇴
    public ResponseEntity<Void> deleteUser(@RequestBody DeleteUserDto deleteUserDto){
        userservice.deleteUser(deleteUserDto);
        return ResponseEntity.ok().build();
    }

    //@GET("/notification") 푸시 알림 정보

}
