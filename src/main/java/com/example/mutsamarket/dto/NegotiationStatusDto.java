package com.example.mutsamarket.dto;

import com.example.mutsamarket.entity.NegotiationEntity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class NegotiationStatusDto {
    //ietmId, 제안가격 제외
    private Long id;

    @NotBlank(message = "공백이거나 입력하지 않았습니다.") @Pattern(regexp = "^(확정|수락|거절)$", message = "상태는 확정, 수락, 거절 중 하나여야 합니다.")
    private String status;

    @NotBlank(message = "공백이거나 입력하지 않았습니다.")
    private String writer;

    @NotBlank(message = "공백이거나 입력하지 않았습니다.")
    private String password;

    public static NegotiationStatusDto fromEntity(NegotiationEntity entity){
        NegotiationStatusDto dto = new NegotiationStatusDto();
        dto.setId(entity.getId());
        dto.setStatus(entity.getStatus());
        dto.setWriter(entity.getWriter());
        dto.setPassword(entity.getPassword());
        return dto;
    }
}
