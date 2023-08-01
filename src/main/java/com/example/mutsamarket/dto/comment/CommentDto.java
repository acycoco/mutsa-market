package com.example.mutsamarket.dto.comment;

import com.example.mutsamarket.entity.CommentEntity;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentDto {
    private Long id;
    private Long itemId;

    @NotBlank(message = "공백이거나 입력하지 않았습니다.")
    private String writer;

    @NotBlank(message = "공백이거나 입력하지 않았습니다.")
    private String password;

    @NotBlank(message = "공백이거나 입력하지 않았습니다.")
    private String content;

    private String reply;

    public static CommentDto fromEntity(CommentEntity entity){
        CommentDto dto = new CommentDto();
        dto.setId(entity.getId());
        dto.setItemId(entity.getItemId());
        dto.setWriter(entity.getWriter());
        dto.setPassword(entity.getPassword());
        dto.setContent(entity.getContent());
        dto.setReply(entity.getReply());
        return dto;
    }
}
