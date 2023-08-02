package com.example.mutsamarket.controller;

import com.example.mutsamarket.dto.*;
import com.example.mutsamarket.dto.Negotiation.NegotiationDto;
import com.example.mutsamarket.dto.Negotiation.NegotiationGetDto;
import com.example.mutsamarket.dto.Negotiation.NegotiationStatusDto;
import com.example.mutsamarket.service.NegotiationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items/{itemId}/proposals")
public class NegotiationController {
    private final NegotiationService service;

    //구매제안 등록
    //POST /items/{itemId}/proposals
    //유효성 검증 NegotiationDto
    @PostMapping
    public ResponseEntity<ResponseDto> create(
            @PathVariable("itemId") Long itemId, @Valid @RequestBody NegotiationDto negotiationDto
    ) {
        this.service.createProposal(itemId, negotiationDto);
        ResponseDto response = new ResponseDto();
        response.setMessage("구매 제안이 등록되었습니다.");
        return ResponseEntity
                .ok(response);
    }

    //구매제안 조회
    //GET /items/{itemId}/proposals?page=1
    //반환값 Page<NegotiationGetDto>
    @GetMapping
    public ResponseEntity<Page<NegotiationGetDto>> readAll(
            @PathVariable("itemId") Long itemId,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "limit", defaultValue = "20") Integer limit
    ) {
        return ResponseEntity
                .ok(this.service.readAllProposal(itemId, page, limit));
    }

    //구매제안 수정
    //PUT /items/{itemId}/proposals/{proposalId}
    //유효성 검증 NegotiationDto
    @PutMapping("/{proposalId}")
    public ResponseEntity<ResponseDto> update(
            @PathVariable("itemId") Long itemId,
            @PathVariable("proposalId") Long proposalId,
            @Valid @RequestBody NegotiationDto negotiationDto
    ) {
        // 제안 수정
        this.service.updateProposal(itemId, proposalId, negotiationDto);
        ResponseDto response = new ResponseDto();
        response.setMessage("제안이 수정되었습니다.");
        return ResponseEntity
                .ok(response);


    }

    //구매제안 삭제
    //DELETE /items/{itemId}/proposals/{proposalId}
    //유효성 검증 DeleteDto
    @DeleteMapping("/{proposalId}")
    public ResponseEntity<ResponseDto> delete(
            @PathVariable("itemId") Long itemId,
            @PathVariable("proposalId") Long proposalId,
            @Valid @RequestBody DeleteDto deleteDto
    ) {
        this.service.deleteProposal(itemId, proposalId, deleteDto);
        ResponseDto response = new ResponseDto();
        response.setMessage("제안을 삭제했습니다.");
        return ResponseEntity
                .ok(response);
    }

    //구매 제안 수락 or 거절 , 확정
    //PUT /items/{itemId}/proposals/{proposalId}/status
    //유효성 검증 NegotiationStatusDto
    @PutMapping("/{proposalId}/status")
    public ResponseEntity<ResponseDto> updateStatus(
            @PathVariable("itemId") Long itemId,
            @PathVariable("proposalId") Long proposalId,
            @Valid @RequestBody NegotiationStatusDto negotiationStatusDto
    ) {

        // 구매 확정
        if (negotiationStatusDto.getStatus().equals("확정")) {
            this.service.confirmProposal(itemId, proposalId, negotiationStatusDto);
            ResponseDto response = new ResponseDto();
            response.setMessage("구매가 확정되었습니다.");
            return ResponseEntity
                    .ok(response);
        }
        // 제안 수락 or 거절
        else {
            this.service.acceptOrRejectProposal(itemId, proposalId, negotiationStatusDto);
            ResponseDto response = new ResponseDto();
            response.setMessage("제안의 상태가 변경되었습니다.");
            return ResponseEntity
                    .ok(response);
        }

    }


}
