package com.example.popconback.user.service;

import com.example.popconback.user.domain.User;
import com.example.popconback.user.dto.CreateUser.CreateUserDto;
import com.example.popconback.user.dto.CreateUser.ResponsCreateUserDto;
import com.example.popconback.user.dto.DeleteUser.DeleteUserDto;
import com.example.popconback.user.dto.Token.ResponseToken;
import com.example.popconback.user.dto.UpdateUser.ResponseUpdateUserDto;
import com.example.popconback.user.dto.UpdateUser.UpdateUserDto;
import com.example.popconback.user.dto.UserDto;
import com.example.popconback.user.kakao.Outh2;
import com.example.popconback.user.repository.UserRepository;
import com.example.popconback.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private final Outh2 outh2;
    @Value("${jwt.secret}")
    private String secretkey;
    @Value("${app.sec}")
    private String appkey;
    private Long expiredMs = 1000 * 60 * 60l;
    private Long expiredMsRe= expiredMs*24*30;
    public ResponsCreateUserDto CreateUser (CreateUserDto createuserDto){
        UserDto user = new UserDto();
        BeanUtils.copyProperties(createuserDto, user, "hash");// 해시값은 무시하고 복사
        user.setHash(user.hashCode());// 해시값 설정// 이거 위줄 아래줄 순서가 바뀌어서 아무것도 없는 값을 조합해서 해시가 이상하게 뜸

        ResponsCreateUserDto responsDto = new ResponsCreateUserDto();
        Optional<User> optionalUser = userRepository.findById(user.hashCode());
        if(optionalUser.isPresent()){
            responsDto.setEmail(null);
            return responsDto;
        }

        BeanUtils.copyProperties(userRepository.save(user.toEntity()),responsDto );
        return responsDto;
    }


    public ResponseToken login (CreateUserDto createUserDto){// 카카오 토큰을 가지고 와서 여기서 로그인 시켜야함
                // 카카오에 사용자 정보를 요청
                // 그걸로 DB 탐색
                // 사용자가 있으면 있는거 보내고 없으면 DB에 회원 등록하고 보내고 (소셜 로그인이라 회원가입과 분리가 안되어 있어서)
                // 두가지 경우 생각해야함

        if (!createUserDto.getSecret().equals(appkey)){
            return null;
        }

        UserDto user = new UserDto();
        BeanUtils.copyProperties(createUserDto,user);
        user.setHash(user.hashCode());


        String token = JwtUtil.creatJwt(createUserDto.getEmail(),createUserDto.getSocial(), secretkey,expiredMs );
        String Refreshtoken = JwtUtil.creatRefashToken (expiredMsRe,secretkey);

        ResponseToken responseToken = new ResponseToken();
        responseToken.setAcessToken(token);
        responseToken.setRefreshToekn(Refreshtoken);

        user.setRefreshToken(Refreshtoken);

        Optional<User> optionalUser = userRepository.findById(user.hashCode());
        if(!optionalUser.isPresent()){ //없으면 초기값 세팅 후 db에 저장 후 아래에서 토큰 발행
            userRepository.save(user.toEntity());
        }
        //있으면 그냥 토큰만 발행


        return responseToken;
    }

    public ResponseToken refresh(String refreshtoken){// 리프레시 토큰 오면 보내는거
        Optional<User> optionalUser = userRepository.findbyRefreshToken(refreshtoken);
        if(!optionalUser.isPresent()){ //없으면 그냥 보내라
          return null;
        }
        User user = optionalUser.get();
        UserDto dto = new UserDto();
        BeanUtils.copyProperties(user,dto);

        String token = JwtUtil.creatJwt(user.getEmail(),user.getSocial(), secretkey,expiredMs );
        String Refreshtoken = JwtUtil.creatRefashToken (expiredMsRe,secretkey);

        ResponseToken responseToken = new ResponseToken();
        responseToken.setAcessToken(token);
        responseToken.setRefreshToekn(Refreshtoken);
        //user에 refreshtoken 저장하기
        dto.setRefreshToken(refreshtoken);
        userRepository.save(dto.toEntity());

        return responseToken;
    }

    public ResponseUpdateUserDto updateUser(UpdateUserDto updateUserDto, int hash){
        Optional<User> optionalUser = userRepository.findById(hash);
        if (!optionalUser.isPresent()){
            throw new EntityNotFoundException("User not present in the database");
        }
        User user = optionalUser.get();
        UserDto userdto = new UserDto();
        userdto.setHash(user.getHash());// 바뀌면 안되는 값들 미리 넣어주기
        userdto.setEmail(user.getEmail());
        userdto.setSocial(user.getSocial());
        BeanUtils.copyProperties(updateUserDto, userdto,"hash","email","social");//바뀌면 안되는 값들은 고정

        ResponseUpdateUserDto responsDto = new ResponseUpdateUserDto();
        BeanUtils.copyProperties(userRepository.save(userdto.toEntity()),responsDto);

        return responsDto;
    }

    public void deleteUser(int hash){
        userRepository.deleteById(hash);
    }

    public List<UserDto> getAllUser(){
        List<User> list = userRepository.findAll();
        List<UserDto> responlist = new ArrayList<>();

        for (User user:list) {
            UserDto ruser = new UserDto();
            BeanUtils.copyProperties(user,ruser);
            responlist.add(ruser);
        }

        return responlist;
    }

}
