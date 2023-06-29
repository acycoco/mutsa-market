package com.example.mutsamarket.service;

import com.example.mutsamarket.dto.ItemDto;
import com.example.mutsamarket.entity.ItemEntity;
import com.example.mutsamarket.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository repository;


    public ItemDto addItem(ItemDto dto){
        ItemEntity entity = new ItemEntity();
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setImageUrl(dto.getImageUrl());
        entity.setMinPriceWanted(dto.getMinPriceWanted());
        entity.setStatus(dto.getStatus());
        entity.setWriter(dto.getWriter());
        entity.setPassword(dto.getPassword());

        return ItemDto.fromEntity(repository.save(entity));
    }
}
