package com.example.mutsamarket.controller;


import com.example.mutsamarket.dto.DeleteDto;
import com.example.mutsamarket.dto.ItemDto;
import com.example.mutsamarket.dto.ItemGetDto;
import com.example.mutsamarket.dto.ResponseDto;
import com.example.mutsamarket.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService service;

    //물품 등록
    // POST /items
    //유효성 검증 ItemDto
    @PostMapping
    public ResponseEntity<ResponseDto> create(@Valid @RequestBody ItemDto dto) {
        this.service.addItem(dto);
        ResponseDto response = new ResponseDto();
        response.setMessage("등록이 완료되었습니다. ");

        return ResponseEntity
                .ok(response);
    }

    //물품 전체 조회, 페이지 단위 조회
    // GET /items?page={page}&limit={limit}
    //반환 값 Page<ItemGetDto>
    @GetMapping
    public ResponseEntity<Page<ItemGetDto>> readAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "limit", defaultValue = "20") Integer limit
    ){
        return ResponseEntity
                .ok(service.readAllItem(page, limit));
    }

    // 물품 단일 조회
    // GET /items/{itemId}
    //반환 값 ItemGetDto
    @GetMapping("/{itemId}")
    public ResponseEntity<ItemGetDto> read(@PathVariable("itemId") Long itemId){
        return ResponseEntity
                .ok(this.service.readItem(itemId));
    }

    //물품 정보 수정
    // PUT /items/{itemId}
    //유효성 검증 itemDto
    @PutMapping("/{itemId}")
    public ResponseEntity<ResponseDto> update(@PathVariable("itemId") Long itemId, @Valid @RequestBody ItemDto itemDto){
        this.service.updateItem(itemId, itemDto);
        ResponseDto response = new ResponseDto();
        response.setMessage("물품이 수정되었습니다.");
        return ResponseEntity
                .ok(response);
    }

    //물품 이미지 수정
    //PUT /items/{itemId}/image

    @PutMapping(value = "/{itemId}/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ResponseDto> updateImage(
            @PathVariable("itemId") Long itemId,
            @RequestParam("image") MultipartFile image,
            @RequestParam("writer") String writer,
            @RequestParam("password") String password
    ){
        this.service.updateItemImage(itemId, image, writer, password);
        ResponseDto response = new ResponseDto();
        response.setMessage("이미지가 등록되었습니다.");
        return ResponseEntity
                .ok(response);
    }
    //물품 삭제
    // DELETE /items/{itemId}
    //유효성 검증 DeleteDto
    @DeleteMapping("/{itemId}")
    public ResponseEntity<ResponseDto> delete(@PathVariable("itemId") Long itemId, @Valid @RequestBody DeleteDto deleteDto){
        this.service.deleteItem(itemId, deleteDto);
        ResponseDto response = new ResponseDto();
        response.setMessage("물품을 삭제했습니다.");
        return ResponseEntity
                .ok(response);
    }

}
