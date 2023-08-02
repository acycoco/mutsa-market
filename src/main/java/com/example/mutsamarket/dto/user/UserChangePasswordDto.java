package com.example.mutsamarket.dto.user;

import com.example.mutsamarket.entity.UserEntity;
import lombok.Data;

@Data
public class UserChangePasswordDto {
    private String oldPassword;
    private String newPassword;
}
