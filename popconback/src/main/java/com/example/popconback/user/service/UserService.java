package com.example.popconback.user.service;

import com.example.popconback.user.domain.User;
import com.example.popconback.user.dto.CreateUser.CreateUserDto;
import com.example.popconback.user.dto.CreateUser.ResponsCreateUserDto;
import com.example.popconback.user.dto.DeleteUser.DeleteUserDto;
import com.example.popconback.user.dto.UpdateUser.ResponseUpdateUserDto;
import com.example.popconback.user.dto.UserDto;
import com.example.popconback.user.kakao.Outh2;
import com.example.popconback.user.repository.UserRepository;
import com.example.popconback.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
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


    public String login (String email, String social, String secret){// 카카오 토큰을 가지고 와서 여기서 로그인 시켜야함
                // 카카오에 사용자 정보를 요청
                // 그걸로 DB 탐색
                // 사용자가 있으면 있는거 보내고 없으면 DB에 회원 등록하고 보내고 (소셜 로그인이라 회원가입과 분리가 안되어 있어서)
                // 두가지 경우 생각해야함

        if (!secret.equals(appkey)){
            return null;
        }
        UserDto user = new UserDto();
        user.setEmail(email);
        user.setSocial(social);
        user.setHash(user.hashCode());

        ResponsCreateUserDto responsDto = new ResponsCreateUserDto();

        Optional<User> optionalUser = userRepository.findById(user.hashCode());

        if(!optionalUser.isPresent()){

            BeanUtils.copyProperties(userRepository.save(user.toEntity()),user );

        }
        String token = JwtUtil.creatJwt(email,social, secretkey,expiredMs );

        return token;
    }

    public ResponseUpdateUserDto updateUser(CreateUserDto createUserDto,int hash){
        Optional<User> optionalUser = userRepository.findById(hash);
        if (!optionalUser.isPresent()){
            throw new EntityNotFoundException("User not present in the database");
        }
        User user = optionalUser.get();
        BeanUtils.copyProperties(createUserDto, user,"hash");

        ResponseUpdateUserDto responsDto = new ResponseUpdateUserDto();
        BeanUtils.copyProperties(userRepository.save(user),responsDto);

        return responsDto;
    }

    public void deleteUser(DeleteUserDto deleteUserDto){
        userRepository.deleteById(deleteUserDto.hashCode());
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
