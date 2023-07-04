package com.example.mutsamarket.controller;

import com.example.mutsamarket.service.NegotiationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class NegotiationController {
    private final NegotiationService service;

    //구매제안 등록
    //POST /items/{itemId}/proposals

    //구매제안 조회
    //GET /items/{itemId}/proposals?writer=jeeho.edu&password=qwerty1234&page=1

    //구매제안 수정
    //PUT /items/{itemId}/proposals/{proposalId}

    //구매제안 삭제
    //DELETE /items/{itemId}/proposals/{proposalId}

    //구매제안 수락 &거절
    //PUT /items/{itemId}/proposals/{proposalId}


    //구매제안 확정
    //PUT /items/{itemId}/proposals/{proposalId}


}
