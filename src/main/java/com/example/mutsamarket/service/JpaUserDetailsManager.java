package com.example.mutsamarket.service;

import com.example.mutsamarket.entity.CustomUserDetails;
import com.example.mutsamarket.entity.UserEntity;
import com.example.mutsamarket.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Slf4j
@Service
public class JpaUserDetailsManager implements UserDetailsManager {
    private final UserRepository userRepository;
    public JpaUserDetailsManager(UserRepository userRepository){
        this.userRepository = userRepository;
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

    //TODO 밑에 더 작성해야됨

    @Override
    public void updateUser(UserDetails user) {

    }

    @Override
    public void deleteUser(String username) {

    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {

    }


}
