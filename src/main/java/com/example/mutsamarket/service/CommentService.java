package com.example.mutsamarket.service;

import com.example.mutsamarket.dto.CommentDto;
import com.example.mutsamarket.entity.CommentEntity;
import com.example.mutsamarket.entity.ItemEntity;
import com.example.mutsamarket.repository.CommentRepository;
import com.example.mutsamarket.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.xml.stream.events.Comment;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final ItemRepository itemRepository;

    //댓글 등록
    public CommentDto createComment(Long itemId, CommentDto commentDto){
        //해당 itemId가 있는지 확인
        if (!itemRepository.existsById(itemId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        CommentEntity comment = new CommentEntity();
        comment.setItemId(itemId);
        comment.setWriter(commentDto.getWriter());
        comment.setPassword(commentDto.getPassword());
        comment.setContent(commentDto.getContent());
        comment.setReply(commentDto.getReply());
        return CommentDto.fromEntity(commentRepository.save(comment));
    }

    //페이지 단위 댓글 조회
    public Page<CommentDto> readCommentAll(Long itemId, Integer pageNum, Integer pageSize){
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by("id").ascending());
        Page<CommentEntity> commentEntityList = commentRepository.findAllByItemId(itemId, pageable);
        return commentEntityList.map(CommentDto::fromEntity);

    }


    //댓글 수정 => 댓글을 단 사람(comment)이 수정할 수 있음
    public CommentDto updateComment(Long itemId, Long commentId, CommentDto dto){

        Optional<CommentEntity> optionalComment = commentRepository.findById(commentId);
        //commentId가 존재하는지 확인
        if (optionalComment.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        CommentEntity comment = optionalComment.get();

        //해당 댓글이 해당 item의 댓글이 맞는지 확인
        if (!comment.getItemId().equals(itemId))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        //댓글 비밀번호 확인
        if (!comment.getPassword().equals(dto.getPassword()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        comment.setWriter(dto.getWriter());
        comment.setContent(dto.getContent());

        return CommentDto.fromEntity(commentRepository.save(comment));
    }

    //답글 수정 => 물품을 등록한사람(item)이 수정할 수 있음
    public CommentDto updateCommentReply(Long itemId, Long commentId, CommentDto dto){
        Optional<CommentEntity> optionalComment = commentRepository.findById(commentId);
        //commentId가 존재하는지 확인
        if (optionalComment.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        CommentEntity comment = optionalComment.get();

        //해당 댓글이 해당 item의 댓글이 맞는지 확인
        if (!comment.getItemId().equals(itemId))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        Optional<ItemEntity> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        ItemEntity item = optionalItem.get();
        // 물품 등록한 사람 => 비밀번호 확인
        if (!item.getPassword().equals(dto.getPassword()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        comment.setReply(dto.getReply());

        return CommentDto.fromEntity(commentRepository.save(comment));
    }

    //댓글 삭제
    //비밀번호 확인
    public void deleteComment(Long itemId, Long commentId, CommentDto dto){
        Optional<CommentEntity> optionalComment = commentRepository.findById(commentId);
        //commentId가 존재하는지 확인
        if (optionalComment.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        //해당 댓글이 해당 item의 댓글이 맞는지 확인
        CommentEntity comment = optionalComment.get();
        if (!comment.getItemId().equals(itemId))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        //비밀번호확인
        if (!comment.getPassword().equals(dto.getPassword()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        //삭제한다.
        commentRepository.deleteById(commentId);

    }
}
