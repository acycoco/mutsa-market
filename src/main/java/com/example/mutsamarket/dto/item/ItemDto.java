package com.example.mutsamarket.dto.item;


import com.example.mutsamarket.entity.ItemEntity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemDto {
    private Long id;

    @NotBlank(message = "공백이거나 입력하지 않았습니다.")
    private String title;

    @NotBlank(message = "공백이거나 입력하지 않았습니다.")
    private String description;

    private String imageUrl;

    @NotNull(message = "입력하지 않았습니다.") @Min(value = 0, message = "최소 0원이상이어야 합니다.")
    private Integer minPriceWanted;

    private String status;

    private String writer;

    private String password;

    public static ItemDto fromEntity(ItemEntity entity){
        ItemDto dto = new ItemDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setImageUrl(entity.getImageUrl());
        dto.setMinPriceWanted(entity.getMinPriceWanted());
        dto.setStatus(entity.getStatus());
        dto.setWriter(entity.getWriter());
        dto.setPassword(entity.getPassword());
        return dto;
    }
}
