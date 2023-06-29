package com.example.mutsamarket.service;

import com.example.mutsamarket.dto.ItemDto;
import com.example.mutsamarket.entity.ItemEntity;
import com.example.mutsamarket.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository repository;


    //물품 등록
    //필수: 제목, 설명, 최소 가격, 작성자, 비밀번호
    public ItemDto addItem(ItemDto dto){
        ItemEntity entity = new ItemEntity();
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setImageUrl(dto.getImageUrl());
        entity.setMinPriceWanted(dto.getMinPriceWanted());
        //최초로 등록할 때 status = "판매중"
        if(entity.getStatus() == null)
            entity.setStatus("판매중");
        entity.setWriter(dto.getWriter());
        entity.setPassword(dto.getPassword());

        return ItemDto.fromEntity(repository.save(entity));
    }

    //물품 전체 조회  페이지 단위 조회도 가능
    public List<ItemDto> readAllItem() {
        List<ItemEntity> itemEntityList = repository.findAll();
        List<ItemDto> itemDtoList = new ArrayList<>();
        for(ItemEntity entity: itemEntityList){
            itemDtoList.add(ItemDto.fromEntity(entity));
        }
        return itemDtoList;
    }
    //물품 단일 조회
    public ItemDto readItem(Long id){
        Optional<ItemEntity> optionalItem = repository.findById(id);

        if (optionalItem.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        ItemEntity item = optionalItem.get();
        return ItemDto.fromEntity(item);
    }

    //물품 수정
    public ItemDto updateItem(Long id, ItemDto dto){
        Optional<ItemEntity> optionalItem = repository.findById(id);
        if(optionalItem.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        ItemEntity item = optionalItem.get();
        // 비밀번호 확인
        if (!dto.getPassword().equals(item.getPassword()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        item.setTitle(dto.getTitle());
        item.setDescription(dto.getDescription());
        item.setImageUrl(dto.getImageUrl());
        item.setMinPriceWanted(dto.getMinPriceWanted());
        item.setStatus(dto.getStatus());
        item.setWriter(dto.getWriter());
        //비밀번호는 수정하려는 본인이 맞는 지 확인하려는 용도
        return ItemDto.fromEntity(repository.save(item));
    }

    // 이미지 첨부
    // 비밀번호 확인


    //물품 삭제
    public void deleteItem(Long id, ItemDto dto){
        Optional<ItemEntity> optionalItem = repository.findById(id);
        if (optionalItem.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        ItemEntity item = optionalItem.get();
        // 비밀번호 확인
        if (!item.getPassword().equals(dto.getPassword()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        repository.deleteById(id);

    }
}
