package com.example.mutsamarket.service;

import com.example.mutsamarket.entity.CustomUserDetails;
import com.example.mutsamarket.entity.UserEntity;
import com.example.mutsamarket.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static com.example.mutsamarket.config.UserUtils.getCurrentUser;

@Slf4j
@Service
public class JpaUserDetailsManager implements UserDetailsManager {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    public JpaUserDetailsManager(UserRepository userRepository, PasswordEncoder passwordEncoder, UserService userService){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

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

        //username이 존재하는지 확인
        if (!userExists(username)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        userRepository.deleteByUsername(username);

        log.info("delete user : {}", username);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        log.info("try change password : {}",  getCurrentUser().getUsername());

        //원래 비밀번호와 oldPassword가 일치하지 않은 경우 에러
        if (!userService.checkPassword(oldPassword)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        //기존의 저장되어 있던 userEntity 불러오기
        Optional<UserEntity> optionalUser = this.userRepository.findByUsername(getCurrentUser().getUsername());

        //userEntity가 존재하지 않으면 에러
        if (optionalUser.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        UserEntity orginalUser = optionalUser.get();

        //기존의 userEntity에 password만 변경
        orginalUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(orginalUser);
        log.info("change password - user : {}", getCurrentUser().getUsername());
    }






}
