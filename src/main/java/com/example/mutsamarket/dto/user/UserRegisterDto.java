package com.example.mutsamarket.dto.user;

import com.example.mutsamarket.entity.UserEntity;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRegisterDto {
    private Long id;

    @NotBlank(message = "공백이거나 입력하지 않았습니다.")
    private String username;
    @NotBlank(message = "공백이거나 입력하지 않았습니다.")
    private String password;
    @NotBlank(message = "공백이거나 입력하지 않았습니다.")
    private String passwordCheck;
    private String phone;
    private String email;
    private String address;

}
