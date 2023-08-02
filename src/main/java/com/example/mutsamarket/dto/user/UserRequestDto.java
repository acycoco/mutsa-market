package com.example.mutsamarket.dto.user;

import com.example.mutsamarket.entity.UserEntity;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRequestDto {
    private Long id;

    @NotBlank(message = "공백이거나 입력하지 않았습니다.")
    private String password;
    private String phone;
    private String email;
    private String address;

    public static UserRequestDto fromEntity(UserEntity entity){
        UserRequestDto dto = new UserRequestDto();
        dto.setId(entity.getId());
        dto.setPassword(entity.getPassword());
        dto.setPhone(entity.getPhone());
        dto.setEmail(entity.getEmail());
        dto.setAddress(entity.getAddress());
        return dto;
    }
}
