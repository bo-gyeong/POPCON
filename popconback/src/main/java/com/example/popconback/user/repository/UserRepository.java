package com.example.popconback.user.repository;

import com.example.popconback.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
//    User findOne(UserId id);
}
