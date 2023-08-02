package com.example.mutsamarket.dto.user;

import com.example.mutsamarket.entity.UserEntity;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserChangePasswordDto {
    @NotBlank(message = "공백이거나 입력하지 않았습니다.")
    private String oldPassword;
    @NotBlank(message = "공백이거나 입력하지 않았습니다.")
    private String newPassword;
}
