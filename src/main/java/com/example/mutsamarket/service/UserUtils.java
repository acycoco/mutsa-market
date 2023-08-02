package com.example.mutsamarket.service;

import com.example.mutsamarket.entity.CustomUserDetails;
import com.example.mutsamarket.entity.UserEntity;
import com.example.mutsamarket.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;


@Slf4j
@Service
public class UserUtils {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserUtils(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //username으로 Entity에 저장되어있는 비밀번호랑 비교
    public String getPassword(){
        //기존의 저장되어 있던 userEntity 불러오기
        Optional<UserEntity> optionalUser = this.userRepository.findByUsername(getCurrentUser().getUsername());

        //userEntity가 존재하지 않으면 에러
        if (optionalUser.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        UserEntity user = optionalUser.get();
        return user.getPassword();
    }
    public boolean checkPassword(String password){
        //기존의 저장되어 있던 userEntity 불러오기
        Optional<UserEntity> optionalUser = this.userRepository.findByUsername(getCurrentUser().getUsername());

        //userEntity가 존재하지 않으면 에러
        if (optionalUser.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        UserEntity user = optionalUser.get();

        return passwordEncoder.matches(password, user.getPassword());

    }

    //인증정보로 UserDetails를 반환하는 메소드
    public UserDetails getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //인증이 되지 않은 사용자의 경우 에러
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);


        return (UserDetails) authentication.getPrincipal();
    }

    //인증 정보 가져와서 userRepostory에 저장되어있는
    // userEntity를 반환하는 메소드
    public Optional<UserEntity> getUserEntity(UserRepository userRepository){
        String username =  getCurrentUser().getUsername();
        return userRepository.findByUsername(username);
    }
}
