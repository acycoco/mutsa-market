package com.example.mutsamarket.dto;

import com.example.mutsamarket.entity.NegotiationEntity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NegotiationGetDto {
    //작성자, 비밀번호, itemId 표시 제외
    private Long id;

    private Integer suggestedPrice;

    private String status;


    public static NegotiationGetDto fromEntity(NegotiationEntity entity){
        NegotiationGetDto dto = new NegotiationGetDto();
        dto.setId(entity.getId());
        dto.setSuggestedPrice(entity.getSuggestedPrice());
        dto.setStatus(entity.getStatus());
        return dto;
    }
}
