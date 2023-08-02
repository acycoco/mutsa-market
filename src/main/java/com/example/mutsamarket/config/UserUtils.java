package com.example.mutsamarket.config;

import com.example.mutsamarket.entity.CustomUserDetails;
import com.example.mutsamarket.entity.UserEntity;
import com.example.mutsamarket.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Slf4j
public class UserUtils {

    //인증정보로 UserDetails를 반환하는 메소드
    public static UserDetails getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //인증이 되지 않은 사용자의 경우 에러
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);


        return (UserDetails) authentication.getPrincipal();
    }

    //인증 정보 가져와서 userRepostory에 저장되어있는
    // userEntity를 반환하는 메소드
    public static Optional<UserEntity> getUserEntity(UserRepository userRepository){
        String username =  getCurrentUser().getUsername();
        return userRepository.findByUsername(username);
    }
}
