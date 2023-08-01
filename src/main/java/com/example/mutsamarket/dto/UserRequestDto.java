package com.example.mutsamarket.dto;

import com.example.mutsamarket.entity.UserEntity;
import lombok.Data;

@Data
public class UserRequestDto {
    private Long id;
    private String username;
    private String password;

    public static UserRequestDto fromEntity(UserEntity entity){
        UserRequestDto dto = new UserRequestDto();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setPassword(entity.getPassword());
        return dto;
    }
}
