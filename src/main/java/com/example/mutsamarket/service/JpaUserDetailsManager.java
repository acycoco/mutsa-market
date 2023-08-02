package com.example.mutsamarket.service;

import com.example.mutsamarket.entity.*;
import com.example.mutsamarket.repository.CommentRepository;
import com.example.mutsamarket.repository.ItemRepository;
import com.example.mutsamarket.repository.NegotiationRepository;
import com.example.mutsamarket.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class JpaUserDetailsManager implements UserDetailsManager {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserUtils userUtils;
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final NegotiationRepository negotiationRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) throw new UsernameNotFoundException(username);
        UserEntity user = optionalUser.get();

        return CustomUserDetails.fromEntity(user);
    }

    @Override
    public void createUser(UserDetails user) {
        log.info("try create user : {}", user.getUsername());
        //username 중복시 에러
        if (userExists(user.getUsername())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        try {
            //userEntity로 바꾸고 저장
            this.userRepository.save(((CustomUserDetails)user).newEntity());
        } catch(ClassCastException e){
            log.error("failed to cast to {}", CustomUserDetails.class);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public boolean userExists(String username) {
        log.info("check if exists user : {}", username);
        return userRepository.existsByUsername(username);
    }


    @Override
    public void updateUser(UserDetails user) {
        log.info("try update user : {}", user.getUsername());
        //기존의 저장되어 있던 userEntity 불러오기
        Optional<UserEntity> optionalUser = this.userRepository.findByUsername(user.getUsername());

        //userEntity가 존재하지 않으면 에러
        if (optionalUser.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        UserEntity orginalUser = optionalUser.get();

        try {
            UserEntity newUser = ((CustomUserDetails) user).newEntity();

            //3가지만 바꿀 수 있음
            orginalUser.setPhone(newUser.getPhone());
            orginalUser.setEmail(newUser.getEmail());
            orginalUser.setAddress(newUser.getAddress());

            //업데이트
            this.userRepository.save(orginalUser);

        } catch(ClassCastException e){
            log.error("failed to cast to {}", CustomUserDetails.class);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Override
    @Transactional
    public void deleteUser(String username) {
        log.info("try delete user : {}", username);

        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);

        //user가 존재하는지 확인
        if (optionalUser.isEmpty()) throw new UsernameNotFoundException(username);
        UserEntity user = optionalUser.get();

        //user의 물품 삭제
        for (ItemEntity item: user.getItems()){
            item.setUser(null);
            itemRepository.delete(item);
        }

        //user의 댓글 삭제
        for (CommentEntity comment: user.getComments()){
            comment.setUser(null);
            commentRepository.delete(comment);
        }

        //user의 구매 제안 삭제
        for (NegotiationEntity negotiation: user.getNegotiations()){
            negotiation.setUser(null);
            negotiationRepository.delete(negotiation);
        }

        userRepository.deleteByUsername(username);

        log.info("delete user : {}", username);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        log.info("try change password : {}",  userUtils.getCurrentUser().getUsername());

        //원래 비밀번호와 oldPassword가 일치하지 않은 경우 에러
        if (!userUtils.checkPassword(oldPassword)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        //기존의 저장되어 있던 userEntity 불러오기
        Optional<UserEntity> optionalUser = this.userRepository.findByUsername(userUtils.getCurrentUser().getUsername());

        //userEntity가 존재하지 않으면 에러
        if (optionalUser.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        UserEntity orginalUser = optionalUser.get();

        //기존의 userEntity에 password만 변경
        orginalUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(orginalUser);
        log.info("change password - user : {}", userUtils.getCurrentUser().getUsername());
    }






}
