package com.example.mutsamarket.dto;

import com.example.mutsamarket.entity.CommentEntity;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentGetDto {
    //작성자, 비밀번호, itemId 표시 제외
    private Long id;

    private String content;

    private String reply;

    public static CommentGetDto fromEntity(CommentEntity entity){
        CommentGetDto dto = new CommentGetDto();
        dto.setId(entity.getId());
        dto.setContent(entity.getContent());
        dto.setReply(entity.getReply());
        return dto;
    }
}
