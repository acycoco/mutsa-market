package com.example.mutsamarket.dto.Negotiation;


import com.example.mutsamarket.entity.NegotiationEntity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NegotiationDto {

    private Long id;
    private Long itemId;

    @NotNull(message = "입력하지 않았습니다.") @Min(value = 0, message = "최소 0원이상이어야 합니다.")
    private Integer suggestedPrice;

    private String status;

    @NotBlank(message = "공백이거나 입력하지 않았습니다.")
    private String writer;

    @NotBlank(message = "공백이거나 입력하지 않았습니다.")
    private String password;

    public static NegotiationDto fromEntity(NegotiationEntity entity){
        NegotiationDto dto = new NegotiationDto();
        dto.setId(entity.getId());
        dto.setItemId(entity.getItemId());
        dto.setSuggestedPrice(entity.getSuggestedPrice());
        dto.setStatus(entity.getStatus());
        dto.setWriter(entity.getWriter());
        dto.setPassword(entity.getPassword());
        return dto;
    }
}
