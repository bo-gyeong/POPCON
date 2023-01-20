package com.example.popconback.user.dto;

import lombok.Data;

import java.util.Objects;

@Data
public class DeleteUserDto {
    private String email;

    private String social;

    @Override
    public int hashCode() {
        return Objects.hash(email,social);
    }
}
