package com.example.mutsamarket.dto;

import com.example.mutsamarket.entity.ItemEntity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeleteDto {
    //삭제시 필요한 dto -> 작성자, 비밀번호 유효성 검증
    private Long id;

    private String writer;

    private String password;

    public static DeleteDto fromEntity(ItemEntity entity){
        DeleteDto dto = new DeleteDto();
        dto.setId(entity.getId());
        dto.setWriter(entity.getWriter());
        dto.setPassword(entity.getPassword());
        return dto;
    }
}
