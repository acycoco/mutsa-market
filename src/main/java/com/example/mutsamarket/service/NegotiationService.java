package com.example.mutsamarket.service;

import com.example.mutsamarket.dto.DeleteDto;
import com.example.mutsamarket.dto.NegotiationDto;
import com.example.mutsamarket.dto.NegotiationGetDto;
import com.example.mutsamarket.dto.NegotiationStatusDto;
import com.example.mutsamarket.entity.ItemEntity;
import com.example.mutsamarket.entity.NegotiationEntity;
import com.example.mutsamarket.repository.ItemRepository;
import com.example.mutsamarket.repository.NegotiationRepository;
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
public class NegotiationService {
    private final NegotiationRepository negotiationRepository;
    private final ItemRepository itemRepository;

    //구매제안 등록
    public NegotiationDto createProposal(Long itemId, NegotiationDto dto){
        //itemId를 찾을 수 없으면 NOT FOUND
        if (!itemRepository.existsById(itemId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        //새 NegotiationEntity 생성
        NegotiationEntity negotiation = new NegotiationEntity();
        negotiation.setItemId(itemId);
        negotiation.setSuggestedPrice(dto.getSuggestedPrice());
        //등록할 때, status = "제안"
        if (negotiation.getStatus() == null)
            negotiation.setStatus("제안");
        negotiation.setWriter(dto.getWriter());
        negotiation.setPassword(dto.getPassword());
        return NegotiationDto.fromEntity(negotiationRepository.save(negotiation));
    }

    //구매제안 조회
    //대상물품 주인 => 작성자, 비밀번호 확인
    //모든 구매제안 조회, 페이지 조회
    public Page<NegotiationGetDto> readAllProposal(
            Long itemId, String writer, String password,
            Integer pageNum, Integer pageSize
    ){
        //대상 물품의 Entity 찾기
        Optional<ItemEntity> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);


        //id 오름차순으로 페이지 단위
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by("id").ascending());


        ItemEntity item = optionalItem.get();

        //물품 주인의 작성자, 비밀번호 확인
        if (item.getWriter().equals(writer) && item.getPassword().equals(password)){
            //물품 주인은 itemId에 대한 모든 구매 제안 조회 / 페이지 단위 조회
            return negotiationRepository.findAllByItemId(itemId, pageable).map(NegotiationGetDto::fromEntity);

        }
        else {
            //제안 등록한 사용자는 itemId에 대한 자신의 구매제안 조회/ 페이지 단위 조회
            //작성자, 비밀번호 확인
            return negotiationRepository.findAllByItemIdAndWriterAndPassword(itemId, writer, password, pageable)
                    .map(NegotiationGetDto::fromEntity);
        }

    }



    //구매제안 수정
    //작성자, 비밀번호 확인
    public NegotiationDto updateProposal(Long itemId, Long proposalId, NegotiationDto dto){
        //NegotiationEntity확인
        Optional<NegotiationEntity> optionalNegotiation = negotiationRepository.findById(proposalId);
        if (optionalNegotiation.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        //itemId가 맞는지 확인
        NegotiationEntity negotiation = optionalNegotiation.get();
        if (!negotiation.getItemId().equals(itemId))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        //작성자, 비밀번호 확인
        if (!negotiation.getWriter().equals(dto.getWriter()) || !negotiation.getPassword().equals(dto.getPassword()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        //수정 가격만 수정
        negotiation.setSuggestedPrice(dto.getSuggestedPrice());
        return NegotiationDto.fromEntity(negotiationRepository.save(negotiation));
    }

    //구매제안 삭제
    //작성자, 비밀번호 확인
    public void deleteProposal(Long itemId, Long proposalId, DeleteDto dto){
        //NegotiationEntity확인
        Optional<NegotiationEntity> optionalNegotiation = negotiationRepository.findById(proposalId);
        if (optionalNegotiation.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        //itemId가 맞는지 확인
        NegotiationEntity negotiation = optionalNegotiation.get();
        if (!negotiation.getItemId().equals(itemId))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        //작성자, 비밀번호 확인
        if (!negotiation.getWriter().equals(dto.getWriter()) || !negotiation.getPassword().equals(dto.getPassword()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        negotiationRepository.deleteById(proposalId);

    }

    //구매제안 수락
    //작성자, 비밀번호 확인
    // 상태 -> "수락" || "거절"
    public NegotiationDto acceptOrRejectProposal(Long itemId, Long proposalId, NegotiationStatusDto dto){
        if (!dto.getStatus().equals("수락") && !dto.getStatus().equals("거절"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        //NegotiationEntity확인
        Optional<NegotiationEntity> optionalNegotiation = negotiationRepository.findById(proposalId);
        if (optionalNegotiation.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        //itemId가 맞는지 확인
        NegotiationEntity negotiation = optionalNegotiation.get();
        if (!negotiation.getItemId().equals(itemId))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        //itemId로 ItemEntity가져오기
        Optional<ItemEntity> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        //대상 물품의 주인 작성자, 비밀번호 확인
        ItemEntity item = optionalItem.get();
        if (!item.getWriter().equals(dto.getWriter()) || !item.getPassword().equals(dto.getPassword()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        //제안의 상태 변경
        negotiation.setStatus(dto.getStatus());

        return NegotiationDto.fromEntity(negotiationRepository.save(negotiation));
    }


    //구매제안 확정
    //작성자, 비밀번호 확인
    // 제안 상태가 "수락"인지 확인
    // 제안 상태 -> "확정"
    // 물품 상태 -> "판매 완료"
    // 나머지 구매제안 -> "거절"
    public NegotiationDto confirmProposal(Long itemId, Long proposalId, NegotiationStatusDto dto){
        //NegotiationEntity확인
        Optional<NegotiationEntity> optionalNegotiation = negotiationRepository.findById(proposalId);
        if (optionalNegotiation.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        //itemId가 맞는지 확인
        NegotiationEntity negotiation = optionalNegotiation.get();
        if (!negotiation.getItemId().equals(itemId))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        //구매제안 등록자의 작성자와 비밀번호 확인
        if (!negotiation.getWriter().equals(dto.getWriter()) || !negotiation.getPassword().equals(dto.getPassword()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        //제안의 상태가 수락이 아닐 경우 실패
        if (!negotiation.getStatus().equals("수락"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        // 제안 상태 -> "확정"
        negotiation.setStatus(dto.getStatus());
        NegotiationEntity confirmedNego = negotiationRepository.save(negotiation);


        //itemId로 ItemEntity가져오기
        Optional<ItemEntity> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        //물품 상태 -> "판매 완료"
        ItemEntity item = optionalItem.get();
        item.setStatus("판매 완료");
        itemRepository.save(item);

        //나머지 제안 상태 -> "거절"
        for(NegotiationEntity nego: negotiationRepository.findAllByItemId(itemId)){
            if (nego.getId().equals(proposalId))
                continue;
            nego.setStatus("거절");
            negotiationRepository.save(nego);
        }

        return NegotiationDto.fromEntity(confirmedNego);

    }
}
