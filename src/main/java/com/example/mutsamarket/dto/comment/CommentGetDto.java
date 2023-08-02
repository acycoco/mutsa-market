package com.example.mutsamarket.dto.comment;

import com.example.mutsamarket.entity.CommentEntity;
import lombok.Data;

@Data
public class CommentGetDto {
    //비밀번호, itemId 표시 제외
    private Long id;

    private String username;

    private String content;

    private String reply;

    public static CommentGetDto fromEntity(CommentEntity entity){
        CommentGetDto dto = new CommentGetDto();
        dto.setId(entity.getId());
        if (entity.getUser() != null){
            dto.setUsername(entity.getUser().getUsername());
        }
        dto.setContent(entity.getContent());
        dto.setReply(entity.getReply());
        return dto;
    }
}
