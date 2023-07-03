package com.example.mutsamarket.repository;

import com.example.mutsamarket.entity.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    //itemId애 대한 페이지 단위로 모든 댓글 찾기
    Page<CommentEntity> findAllByItemId(Long itemId, Pageable pageable);

}
