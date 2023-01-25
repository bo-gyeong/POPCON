package com.example.popconback.user.controller;


import com.example.popconback.user.dto.CreateUser.CreateUserDto;
import com.example.popconback.user.dto.CreateUser.ResponsCreateUserDto;
import com.example.popconback.user.dto.DeleteUser.DeleteUserDto;
import com.example.popconback.user.dto.UpdateUser.ResponseUpdateUserDto;
import com.example.popconback.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(value = "UserController")
@SwaggerDefinition(tags = {@Tag(name = "UserController",
        description = "유저 컨트롤러")})
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/user")
public class UserController {

    private final UserService userservice;

    @ApiOperation(value = "createUserK",
            notes = "회원 정보 DB에 저장(카카오)",
            httpMethod = "POST")
    @PostMapping("/login/kakao") // 회원 정보 DB에 저장(카카오)
    public ResponseEntity<ResponsCreateUserDto> createUserK(@RequestBody CreateUserDto createUserDto){
        return ResponseEntity.ok(userservice.CreateUser(createUserDto));
    }

    @ApiOperation(value = "createUserN",
            notes = "회원 정보 DB에 저장(네이버)",
            httpMethod = "POST")
    @PostMapping("/login/naver") // 회원 정보 DB에 저장(네이버)
    public ResponseEntity<ResponsCreateUserDto> createUserN(@RequestBody CreateUserDto createUserDto){
        return ResponseEntity.ok(userservice.CreateUser(createUserDto));
    }

    @ApiOperation(value = "updateUser",
            notes = "회원 정보 수정",
            httpMethod = "POST")
    @PostMapping("/update/{hash}")// 회원 정보 수정
    public ResponseEntity<ResponseUpdateUserDto> updateUser(@RequestBody CreateUserDto createUserDto, @PathVariable int hash){
        return ResponseEntity.ok(userservice.updateUser(createUserDto,hash));
    }

    @ApiOperation(value = "deleteUser",
            notes = "회원 탈퇴",
            httpMethod = "DELETE")
    @DeleteMapping("/withdrawal") //회원 탈퇴
    public ResponseEntity<Void> deleteUser(@RequestBody DeleteUserDto deleteUserDto){
        userservice.deleteUser(deleteUserDto);
        return ResponseEntity.ok().build();
    }



    //@GET("/notification") 푸시 알림 정보

}
