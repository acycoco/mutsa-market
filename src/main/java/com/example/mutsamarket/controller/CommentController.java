package com.example.mutsamarket.controller;

import com.example.mutsamarket.dto.CommentDto;
import com.example.mutsamarket.dto.ResponseDto;
import com.example.mutsamarket.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/items/{itemId}/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService service;

    //댓글 등록
    //POST /items/{itemId}/comments
    @PostMapping
    public ResponseEntity<ResponseDto> create(
            @PathVariable("itemId") Long itemId,
            @Valid @RequestBody CommentDto commentDto
    ){
        this.service.createComment(itemId, commentDto);
        ResponseDto response = new ResponseDto();
        response.setMessage("댓글이 등록되었습니다.");
        return ResponseEntity
                .ok(response);
    }

    //페이지 단위 댓글 조회
    //GET /items/{itemId}/comments
    @GetMapping
    public ResponseEntity<Page<CommentDto>> readAll(
            @PathVariable("itemId") Long itemId,
            @RequestParam(value = "page",defaultValue = "0") Integer page,
            @RequestParam(value = "limit", defaultValue = "20") Integer limit
    ){
        return ResponseEntity.ok(this.service.readCommentAll(itemId, page, limit));

    }
    //댓글 수정
    //PUT /items/{itemId}/comments/{commentId}
    @PutMapping("/{commentId}")
    public ResponseEntity<ResponseDto> update(
            @PathVariable("itemId") Long itemId,
            @PathVariable("commentId") Long commentId,
            @Valid @RequestBody CommentDto commentDto
    ){
        this.service.updateComment(itemId, commentId, commentDto);
        ResponseDto response = new ResponseDto();
        response.setMessage("댓글이 수정되었습니다.");

        return ResponseEntity
                .ok(response);
    }

    //답글 수정
    //PUT /items/{itemId}/comments/{commentId}/reply
    @PutMapping("/{commentId}/reply")
    public ResponseEntity<ResponseDto> updateReply(
            @PathVariable("itemId") Long itemId,
            @PathVariable("commentId") Long commentId,
            @RequestBody CommentDto commentDto
    ){
        this.service.updateCommentReply(itemId, commentId, commentDto);
        ResponseDto response = new ResponseDto();
        response.setMessage("댓글에 답변이 추가되었습니다.");

        return ResponseEntity
                .ok(response);
    }

    //댓글 삭제
    //DELETE /items/{itemId}/comments/{commentId}
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ResponseDto> delete(
            @PathVariable("itemId") Long itemId,
            @PathVariable("commentId") Long commentId,
            @RequestBody CommentDto commentDto
    ){
        this.service.deleteComment(itemId, commentId, commentDto);
        ResponseDto response = new ResponseDto();
        response.setMessage("댓글을 삭제했습니다.");
        return ResponseEntity
                .ok(response);
    }

}
