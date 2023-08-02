package com.example.mutsamarket.dto.comment;

import com.example.mutsamarket.entity.CommentEntity;
import com.example.mutsamarket.entity.ItemEntity;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentDto {
    private Long id;
    private ItemEntity item;

    private String writer;

    private String password;

    @NotBlank(message = "공백이거나 입력하지 않았습니다.")
    private String content;

    private String reply;

    public static CommentDto fromEntity(CommentEntity entity){
        CommentDto dto = new CommentDto();
        dto.setId(entity.getId());
        dto.setItem(entity.getItem());
        dto.setWriter(entity.getWriter());
        dto.setPassword(entity.getPassword());
        dto.setContent(entity.getContent());
        dto.setReply(entity.getReply());
        return dto;
    }
}
