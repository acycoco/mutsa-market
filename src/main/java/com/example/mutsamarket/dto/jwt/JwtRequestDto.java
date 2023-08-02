package com.example.mutsamarket.dto.jwt;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JwtRequestDto {
    @NotBlank(message = "공백이거나 입력하지 않았습니다.")
    private String username;
    @NotBlank(message = "공백이거나 입력하지 않았습니다.")
    private String password;
}
