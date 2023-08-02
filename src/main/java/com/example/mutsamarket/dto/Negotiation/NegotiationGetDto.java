package com.example.mutsamarket.dto.Negotiation;

import com.example.mutsamarket.entity.NegotiationEntity;
import lombok.Data;

@Data
public class NegotiationGetDto {
    //작성자, 비밀번호, itemId 표시 제외
    private Long id;

    private Integer suggestedPrice;

    private String status;

    private String username;

    public static NegotiationGetDto fromEntity(NegotiationEntity entity){
        NegotiationGetDto dto = new NegotiationGetDto();
        dto.setId(entity.getId());
        dto.setSuggestedPrice(entity.getSuggestedPrice());
        dto.setStatus(entity.getStatus());
        if (entity.getUser() != null){
            dto.setUsername(entity.getUser().getUsername());
        }
        return dto;
    }
}
