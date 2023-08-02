package com.example.mutsamarket.dto.user;

import com.example.mutsamarket.entity.UserEntity;
import lombok.Data;

@Data
public class UserRequestDto {
    private Long id;
    private String username;
    private String password;
    private String phone;
    private String email;
    private String address;

    public static UserRequestDto fromEntity(UserEntity entity){
        UserRequestDto dto = new UserRequestDto();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setPassword(entity.getPassword());
        dto.setPhone(entity.getPhone());
        dto.setEmail(entity.getEmail());
        dto.setAddress(entity.getAddress());
        return dto;
    }
}
