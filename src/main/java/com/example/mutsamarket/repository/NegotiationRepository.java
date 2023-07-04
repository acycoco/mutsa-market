package com.example.mutsamarket.repository;

import com.example.mutsamarket.entity.NegotiationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NegotiationRepository extends JpaRepository<NegotiationEntity, Long> {
    // itemId로 페이지 단위로 조회
    Page<NegotiationEntity> findAllByItemId(Long itemId, Pageable pageable);
    Page<NegotiationEntity> findAllByItemIdAndWriterAndPassword(Long itemId, String writer, String password, Pageable pageable);
    List<NegotiationEntity> findAllByItemId(Long itemId);
}
