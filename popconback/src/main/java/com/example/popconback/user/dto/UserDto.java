package com.example.popconback.user.dto;

import com.example.popconback.user.domain.User;
import lombok.Data;

import java.util.Objects;

@Data
public class UserDto {
    private int hash;
    private String  email;
    private String social;
    private String Token;
    private String refreshToken;
    private int alarm;
    private int Nday;
    private int term;
    private int timezone;
    private int manner_temp;
    private String secret;

    public User toEntity() {
        User build = User.builder()
                .hash(hash)
                .email(email)
                .social(social)
                .Token(Token)
                .refreshtoken(refreshToken)
                .alarm(alarm)
                .Nday(Nday)
                .term(term)
                .timezone(timezone)
                .manner_temp(manner_temp)
                .build();
        return build;
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, social);
    }
}
