package com.example.mutsamarket.dto.user;

import com.example.mutsamarket.entity.UserEntity;
import lombok.Data;

@Data
public class UserRegisterDto {
    private Long id;
    private String username;
    private String password;
    private String passwordCheck;
    private String phone;
    private String email;
    private String address;

}
