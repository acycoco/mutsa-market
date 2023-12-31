package com.example.mutsamarket.service;

import com.example.mutsamarket.dto.DeleteDto;
import com.example.mutsamarket.dto.item.ItemDto;
import com.example.mutsamarket.dto.item.ItemGetDto;
import com.example.mutsamarket.entity.*;
import com.example.mutsamarket.repository.CommentRepository;
import com.example.mutsamarket.repository.ItemRepository;
import com.example.mutsamarket.repository.NegotiationRepository;
import com.example.mutsamarket.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository repository;
    private final CommentRepository commentRepository;
    private final NegotiationRepository negotiationRepository;
    private final UserRepository userRepository;
    private final UserUtils userUtils;

    //물품 등록
    public ItemDto addItem(ItemDto dto){
        ItemEntity entity = new ItemEntity();
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setImageUrl(dto.getImageUrl());
        entity.setMinPriceWanted(dto.getMinPriceWanted());

        //최초로 등록할 때 status = "판매중"
        if(entity.getStatus() == null)
            entity.setStatus("판매중");

        entity.setWriter(dto.getWriter());
        entity.setPassword(dto.getPassword());

        //현재 인증정보로 userRepository에 저장된 userEntity 가져오기
        UserEntity userEntity = userUtils.getUserEntity(userRepository).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        entity.setUser(userEntity);

        return ItemDto.fromEntity(repository.save(entity));
    }

    //물품 전체 조회  페이지 단위 조회도 가능
    //ItemGetDto로 반환 -> 사용자 정보 중 작성자만 표시
    public Page<ItemGetDto> readAllItem(Integer pageNum, Integer pageSize) {

        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by("id").descending());
        Page<ItemEntity> itemEntityList = repository.findAll(pageable);

        Page<ItemGetDto> itemGetDtoPage = itemEntityList.map(ItemGetDto::fromEntity);
        return itemGetDtoPage;
    }

    //물품 단일 조회
    //ItemGetDto로 반환 -> 사용자 정보 중 작성자만 표시
    public ItemGetDto readItem(Long id){
        Optional<ItemEntity> optionalItem = repository.findById(id);

        if (optionalItem.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        ItemEntity item = optionalItem.get();
        return ItemGetDto.fromEntity(item);
    }

    //물품 수정
    public ItemDto updateItem(Long id, ItemDto dto){
        Optional<ItemEntity> optionalItem = repository.findById(id);
        if(optionalItem.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        ItemEntity item = optionalItem.get();

        // 비밀번호 확인 둘다 암호화된 비밀번호
        if (!userUtils.getPassword().equals(item.getUser().getPassword()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        item.setTitle(dto.getTitle());
        item.setDescription(dto.getDescription());
        item.setImageUrl(dto.getImageUrl());
        item.setMinPriceWanted(dto.getMinPriceWanted());
        item.setStatus(dto.getStatus());

        //비밀번호는 수정하려는 본인이 맞는 지 확인하려는 용도
        return ItemDto.fromEntity(repository.save(item));
    }

    // 이미지 첨부
    // 비밀번호 확인
    public ItemDto updateItemImage(Long id, MultipartFile image){
        //해당 item이 있는지 확인
        Optional<ItemEntity> optionalItem = repository.findById(id);
        if (optionalItem.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        ItemEntity item = optionalItem.get();

        // 비밀번호 확인 둘다 암호화된 비밀번호
        if (!userUtils.getPassword().equals(item.getUser().getPassword()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        //이미지 파일 저장하기
        String imageDir = String.format("media/%d/",id);
        try{
            //디렉토리 생성
            Files.createDirectories(Path.of(imageDir));
        } catch (IOException e){
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String imageOriginalFilename = image.getOriginalFilename();
        String[] imageFilenameSplit = imageOriginalFilename.split("\\.");
        String extention = imageFilenameSplit[imageFilenameSplit.length - 1];
        String imageFilename = "image." + extention;
        String imagePath = imageDir + imageFilename;
        log.info(imagePath);

        try{
            image.transferTo(Path.of(imagePath));

        } catch (IOException e){
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        item.setImageUrl(String.format("/static/%d/%s", id, imageFilename));
        return ItemDto.fromEntity(repository.save(item));
    }

    //물품 삭제
    public void deleteItem(Long id, DeleteDto dto){
        //해당 id 찾기
        Optional<ItemEntity> optionalItem = repository.findById(id);
        if (optionalItem.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        ItemEntity item = optionalItem.get();

        // 비밀번호 확인 둘다 암호화된 비밀번호
        if (!userUtils.getPassword().equals(item.getUser().getPassword()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        //해당 물품 판매 글의 댓글들 삭제
        List<CommentEntity> comments = item.getComments();
        for (CommentEntity comment : comments){
            comment.setItem(null);
            commentRepository.delete(comment);
        }

        //해당 물품 판매 글의 구매제안들 삭제
        List<NegotiationEntity> negotiations = item.getNegotiations();
        for (NegotiationEntity negotiation : negotiations){
            negotiation.setItem(null);
            negotiationRepository.delete(negotiation);
        }

        //연관관계 삭제 후 item 삭제
        item.setUser(null);
        repository.deleteById(id);

    }
}
