package com.example.mutsamarket.service;

import com.example.mutsamarket.repository.NegotiationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NegotiationService {
    private final NegotiationRepository negotiationRepository;

    //구매제안 등록
    //등록할 때, status = "제안"

    //구매제안 조회
    //대상물품 주인 => 작성자, 비밀번호 확인
    //모든 구매제안 조회, 페이지 조회

    //구매제안 등록한 사용자 => 작성자, 비밀번호 확인
    //자신의 제안만 조회, 페이지 조회

    //구매제안 수정
    //작성자, 비밀번호 확인

    //구매제안 삭제
    //작성자, 비밀번호 확인

    //구매제안 수락
    //작성자, 비밀번호 확인
    // 상태 -> "수락"

    //구매제안 거절
    //작성자, 비밀번호 확인
    //상태 -> "거절"

    //구매제안 확정
    //작성자, 비밀번호 확인
    // 제안 상태 -> "확정"
    // 물품 상태 -> "판매 완료"
    // 나머지 구매제안 -> "거절"
}
