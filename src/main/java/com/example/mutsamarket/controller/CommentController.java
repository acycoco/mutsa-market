package com.example.mutsamarket.controller;

import com.example.mutsamarket.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/items/{itemId}/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService service;

    //댓글 등록
    //POST /items/{itemId}/comments


    //페이지 단위 댓글 조회
    //GET /items/{itemId}/comments

    //댓글 수정
    //PUT /items/{itemId}/comments/{commentId}
    //비밀번호 확인

    //답글 수정
    //PUT /items/{itemId}/comments/{commentId}/reply
    //물품 등록한 사람 => 비밀번호 확인

    //댓글 삭제
    //DELETE /items/{itemId}/comments/{commentId}

}
