package com.example.mutsamarket.repository;

import com.example.mutsamarket.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    //itemId로 찾기

    //itemId로 존재하는 확인
}
