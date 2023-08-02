package com.example.mutsamarket.service;

import com.example.mutsamarket.entity.UserEntity;
import com.example.mutsamarket.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static com.example.mutsamarket.config.UserUtils.getCurrentUser;

@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //username으로 Entity에 저장되어있는 비밀번호랑 비교
    public boolean checkPassword(String password){
        //기존의 저장되어 있던 userEntity 불러오기
        Optional<UserEntity> optionalUser = this.userRepository.findByUsername(getCurrentUser().getUsername());

        //userEntity가 존재하지 않으면 에러
        if (optionalUser.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        UserEntity user = optionalUser.get();

        return passwordEncoder.matches(password, user.getPassword());

    }
}
