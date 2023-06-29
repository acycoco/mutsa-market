package com.example.mutsamarket.controller;


import com.example.mutsamarket.dto.ItemDto;
import com.example.mutsamarket.dto.ResponseDto;
import com.example.mutsamarket.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService service;

    //물품 등록
    // POST /items
    @PostMapping
    public ResponseDto create(@RequestBody ItemDto dto) {
        this.service.addItem(dto);
        ResponseDto response = new ResponseDto();
        response.setMessage("등록이 완료되었습니다. ");

        return response;
    }

    //물품 전체 조회, 페이지 단위 조회
    // TODO GET /items?page={page}&limit={limit}
    @GetMapping
    public List<ItemDto> readAll(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "limit", required = false) Integer limit
    ){
        return service.readAllItem();
    }

    // 물품 단일 조회
    // GET /items/{itemId}
    @GetMapping("/{itemId}")
    public ItemDto read(@PathVariable("itemId") Long itemId){
        return this.service.readItem(itemId);
    }

    //물품 정보 수정
    // PUT /items/{itemId}
    @PutMapping("/{itemId}")
    public ResponseDto update(@PathVariable("itemId") Long itemId, @RequestBody ItemDto itemDto){
        this.service.updateItem(itemId, itemDto);
        ResponseDto response = new ResponseDto();
        response.setMessage("물품이 수정되었습니다.");
        return response;
    }

    //물품 이미지 수정

    //물품 삭제
    // DELETE /items/{itemId}
    @DeleteMapping("/{itemId}")
    public ResponseDto delete(@PathVariable("itemId") Long itemId, @RequestBody ItemDto itemDto){
        this.service.deleteItem(itemId, itemDto);
        ResponseDto response = new ResponseDto();
        response.setMessage("물품을 삭제했습니다.");
        return response;
    }

}
