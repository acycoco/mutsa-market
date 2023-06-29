package com.example.mutsamarket.controller;


import com.example.mutsamarket.dto.ItemDto;
import com.example.mutsamarket.dto.ResponseDto;
import com.example.mutsamarket.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService service;

    @PostMapping
    public ResponseDto addItem(@RequestBody ItemDto dto) {
        this.service.addItem(dto);
        ResponseDto response = new ResponseDto();
        response.setMessage("등록이 완료되었습니다. ");

        return response;
    }


}
