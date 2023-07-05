package com.example.mutsamarket.service;

import com.example.mutsamarket.dto.DeleteDto;
import com.example.mutsamarket.dto.ItemDto;
import com.example.mutsamarket.dto.ItemGetDto;
import com.example.mutsamarket.entity.ItemEntity;
import com.example.mutsamarket.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository repository;


    //물품 등록
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
    //ItemGetDto로 반환 -> 작성자, 비밀번호 미표시
    public Page<ItemGetDto> readAllItem(Integer pageNum, Integer pageSize) {

        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by("id").descending());
        Page<ItemEntity> itemEntityList = repository.findAll(pageable);

        Page<ItemGetDto> itemGetDtoPage = itemEntityList.map(ItemGetDto::fromEntity);
        return itemGetDtoPage;
    }
    //물품 단일 조회
    //ItemGetDto로 반환 -> 작성자, 비밀번호 미표시
    public ItemGetDto readItem(Long id){
        Optional<ItemEntity> optionalItem = repository.findById(id);

        if (optionalItem.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        ItemEntity item = optionalItem.get();
        return ItemGetDto.fromEntity(item);
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
    public ItemDto updateItemImage(Long id, MultipartFile image, String writer, String password){
        Optional<ItemEntity> optionalItem = repository.findById(id);
        if (optionalItem.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        ItemEntity item = optionalItem.get();
        //비밀번호 확인
        if (!item.getPassword().equals(password))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        //이미지 파일 저장하기
        String imageDir = String.format("media/%d/",id);
        try{
            //디렉토리 생성
            Files.createDirectories(Path.of(imageDir));
        } catch (IOException e){
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String imageOriginalFilename = image.getOriginalFilename();
        String[] imageFilenameSplit = imageOriginalFilename.split("\\.");
        String extention = imageFilenameSplit[imageFilenameSplit.length - 1];
        String imageFilename = "image." + extention;
        String imagePath = imageDir + imageFilename;
        log.info(imagePath);
        try{
            image.transferTo(Path.of(imagePath));

        } catch (IOException e){
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }


        item.setImageUrl(String.format("/static/%d/%s", id, imageFilename));
        return ItemDto.fromEntity(repository.save(item));
    }

    //물품 삭제
    public void deleteItem(Long id, DeleteDto dto){
        Optional<ItemEntity> optionalItem = repository.findById(id);
        if (optionalItem.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        ItemEntity item = optionalItem.get();
        // 비밀번호 확인
        if (!item.getPassword().equals(dto.getPassword()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        repository.deleteById(id);

    }
}
