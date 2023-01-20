package com.example.popconback.user.service;

import com.example.popconback.user.domain.User;
import com.example.popconback.user.dto.CreateUser.CreateUserDto;
import com.example.popconback.user.dto.CreateUser.ResponsCreateUserDto;
import com.example.popconback.user.dto.DeleteUser.DeleteUserDto;
import com.example.popconback.user.dto.UpdateUser.ResponseUpdateUserDto;
import com.example.popconback.user.dto.UserDto;
import com.example.popconback.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public ResponsCreateUserDto CreateUser (CreateUserDto createuserDto){
        User user = new User();
        BeanUtils.copyProperties(createuserDto, user, "hash");// 해시값은 무시하고 복사
        user.setHash(user.hashCode());// 해시값 설정// 이거 위줄 아래줄 순서가 바뀌어서 아무것도 없는 값을 조합해서 해시가 이상하게 뜸

        ResponsCreateUserDto responsDto = new ResponsCreateUserDto();
        Optional<User> optionalUser = userRepository.findById(user.hashCode());
        if(optionalUser.isPresent()){
            responsDto.setEmail(null);
            return responsDto;
        }

        BeanUtils.copyProperties(userRepository.save(user),responsDto );
        return responsDto;
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
