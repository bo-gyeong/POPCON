package com.example.popconback.user.controller;


import com.example.popconback.user.dto.CreateUser.CreateUserDto;
import com.example.popconback.user.dto.CreateUser.ResponsCreateUserDto;
import com.example.popconback.user.dto.DeleteUser.DeleteUserDto;
import com.example.popconback.user.dto.UpdateUser.ResponseUpdateUserDto;
import com.example.popconback.user.service.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api")
public class UserController {

    private final UserService userservice;

    @PostMapping("/user/login") // 회원 정보 DB에 저장(카카오)
    public ResponseEntity<ResponsCreateUserDto> createUserK(@RequestBody CreateUserDto createUserDto){
        return ResponseEntity.ok(userservice.CreateUser(createUserDto));
    }
    @PostMapping("/user/naver-login") // 회원 정보 DB에 저장(네이버)
    public ResponseEntity<ResponsCreateUserDto> createUserN(@RequestBody CreateUserDto createUserDto){
        return ResponseEntity.ok(userservice.CreateUser(createUserDto));
    }
    @PostMapping("/user/update/{hash}")// 회원 정보 수정
    public ResponseEntity<ResponseUpdateUserDto> updateUser(@RequestBody CreateUserDto createUserDto, @PathVariable int hash){
        return ResponseEntity.ok(userservice.updateUser(createUserDto,hash));
    }
    @DeleteMapping("/user/withdrawal") //회원 탈퇴
    public ResponseEntity<Void> deleteUser(@RequestBody DeleteUserDto deleteUserDto){
        userservice.deleteUser(deleteUserDto);
        return ResponseEntity.ok().build();
    }



    //@GET("/notification") 푸시 알림 정보

}
