package com.example.mutsamarket.service;

import com.example.mutsamarket.dto.*;
import com.example.mutsamarket.dto.comment.CommentDto;
import com.example.mutsamarket.dto.comment.CommentGetDto;
import com.example.mutsamarket.dto.comment.CommentReplyDto;
import com.example.mutsamarket.entity.CommentEntity;
import com.example.mutsamarket.entity.ItemEntity;
import com.example.mutsamarket.entity.UserEntity;
import com.example.mutsamarket.repository.CommentRepository;
import com.example.mutsamarket.repository.ItemRepository;
import com.example.mutsamarket.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final ItemRepository itemRepository;
    private final UserUtils userUtils;
    private final UserRepository userRepository;

    //댓글 등록
    public CommentDto createComment(Long itemId, CommentDto commentDto){
        //해당 itemId가 있는지 확인
        Optional<ItemEntity> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        CommentEntity comment = new CommentEntity();
        comment.setItem(optionalItem.get());

        //현재 인증정보로 userRepository에 저장된 userEntity 가져오기
        UserEntity userEntity = userUtils.getUserEntity(userRepository).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        //userEntity등록
        comment.setUser(userEntity);

        comment.setContent(commentDto.getContent());
        comment.setReply(commentDto.getReply());
        return CommentDto.fromEntity(commentRepository.save(comment));
    }

    //페이지 단위 댓글 조회
    public Page<CommentGetDto> readCommentAll(Long itemId, Integer pageNum, Integer pageSize){
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by("id").ascending());
        Page<CommentEntity> commentEntityList = commentRepository.findAllByItemId(itemId, pageable);
        return commentEntityList.map(CommentGetDto::fromEntity);

    }


    //댓글 수정 => 댓글을 단 사람(comment)이 수정할 수 있음
    public CommentDto updateComment(Long itemId, Long commentId, CommentDto dto){

        Optional<CommentEntity> optionalComment = commentRepository.findById(commentId);

        //commentId가 존재하는지 확인
        if (optionalComment.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        CommentEntity comment = optionalComment.get();

        //해당 댓글이 해당 item의 댓글이 맞는지 확인
        if (!comment.getItem().getId().equals(itemId))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        //댓글 비밀번호 확인
        // 비밀번호 확인 둘다 암호화된 비밀번호
        if (!userUtils.getPassword().equals(comment.getUser().getPassword()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        comment.setWriter(dto.getWriter());
        comment.setContent(dto.getContent());

        return CommentDto.fromEntity(commentRepository.save(comment));
    }

    //답글 수정 => 물품을 등록한사람(item)이 수정할 수 있음
    public CommentDto updateCommentReply(Long itemId, Long commentId, CommentReplyDto dto){
        Optional<CommentEntity> optionalComment = commentRepository.findById(commentId);
        //commentId가 존재하는지 확인
        if (optionalComment.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        CommentEntity comment = optionalComment.get();

        //해당 댓글이 해당 item의 댓글이 맞는지 확인
        if (!comment.getItem().getId().equals(itemId))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        Optional<ItemEntity> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        ItemEntity item = optionalItem.get();
        // 물품 등록한 사람 => 비밀번호 확인
        if (!userUtils.getPassword().equals(item.getUser().getPassword()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        comment.setReply(dto.getReply());

        return CommentDto.fromEntity(commentRepository.save(comment));
    }

    //댓글 삭제
    //비밀번호 확인
    public void deleteComment(Long itemId, Long commentId, DeleteDto dto){
        Optional<CommentEntity> optionalComment = commentRepository.findById(commentId);
        //commentId가 존재하는지 확인
        if (optionalComment.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        //해당 댓글이 해당 item의 댓글이 맞는지 확인
        CommentEntity comment = optionalComment.get();
        if (!comment.getItem().getId().equals(itemId))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        //비밀번호확인
        if (!userUtils.getPassword().equals(comment.getUser().getPassword()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        //삭제한다.
        //연관관계 삭제 후 comment삭제
        comment.setItem(null);
        comment.setUser(null);
        commentRepository.deleteById(commentId);

    }
}
