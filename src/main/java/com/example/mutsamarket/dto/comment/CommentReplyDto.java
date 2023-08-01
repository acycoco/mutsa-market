package com.example.mutsamarket.dto.comment;

import com.example.mutsamarket.entity.CommentEntity;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentReplyDto {
    //content, itemId 제외
    // reply 유효성검증 추가
    private Long id;

    @NotBlank(message = "공백이거나 입력하지 않았습니다.")
    private String writer;

    @NotBlank(message = "공백이거나 입력하지 않았습니다.")
    private String password;

    @NotBlank(message = "공백이거나 입력하지 않았습니다.")
    private String reply;

    public static CommentReplyDto fromEntity(CommentEntity entity){
        CommentReplyDto dto = new CommentReplyDto();
        dto.setId(entity.getId());
        dto.setWriter(entity.getWriter());
        dto.setPassword(entity.getPassword());
        dto.setReply(entity.getReply());
        return dto;
    }
}
