package com.example.popconback.user.service;

import com.example.popconback.user.domain.User;
import com.example.popconback.user.dto.CreateUserDto;
import com.example.popconback.user.dto.DeleteUserDto;
import com.example.popconback.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User CreateUser (CreateUserDto createuserDto){
        User user = new User();
        BeanUtils.copyProperties(createuserDto, user, "hash");// 해시값은 무시하고 복사
        user.setHash(user.hashCode());// 해시값 설정// 이거 위줄 아래줄 순서가 바뀌어서 아무것도 없는 값을 조합해서 해시가 이상하게 뜸
        System.out.println(user.getEmail());
        System.out.println(user.getSocial());
        System.out.println(user.getHash());
        System.out.println(user.hashCode());
        return userRepository.save(user);
    }
    public void deleteUser(DeleteUserDto deleteUserDto){
        userRepository.deleteById(deleteUserDto.hashCode());
    }

}
