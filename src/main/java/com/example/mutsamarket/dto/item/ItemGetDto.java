package com.example.mutsamarket.dto.item;

import com.example.mutsamarket.entity.ItemEntity;
import lombok.Data;

@Data
public class ItemGetDto {
    //GET 요청에만 쓰이는 dto -> 작성자와 비밀번호 없음
    private Long id;

    private String title;

    private String description;

    private String imageUrl;

    private Integer minPriceWanted;

    private String status;


    public static ItemGetDto fromEntity(ItemEntity entity){
        ItemGetDto dto = new ItemGetDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setImageUrl(entity.getImageUrl());
        dto.setMinPriceWanted(entity.getMinPriceWanted());
        dto.setStatus(entity.getStatus());
        return dto;
    }
}
