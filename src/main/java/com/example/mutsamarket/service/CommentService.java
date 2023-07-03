package com.example.mutsamarket.service;

import com.example.mutsamarket.repository.CommentRepository;
import com.example.mutsamarket.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final ItemRepository itemRepository;

    //댓글 등록

    //페이지 단위 댓글 조회

    //댓글 수정

    //답글 수정

    //댓글 삭제
}
