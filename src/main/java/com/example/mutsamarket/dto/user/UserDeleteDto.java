package com.example.mutsamarket.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDeleteDto {
    @NotBlank(message = "공백이거나 입력하지 않았습니다.")
    private String password;

}
