package com.example.mutsamarket.controller;

import com.example.mutsamarket.dto.ResponseDto;
import com.example.mutsamarket.entity.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserDetailsManager manager;
    private final PasswordEncoder passwordEncoder;


    public UserController(UserDetailsManager manager, PasswordEncoder passwordEncoder) {
        this.manager = manager;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseDto> registerUser(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("password-check") String passwordCheck
    ){
        if (password.equals(passwordCheck)){
            log.info("password match");
            manager.createUser(CustomUserDetails.builder()
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .build());

            ResponseDto response = new ResponseDto();
            response.setMessage("회원가입이 완료되었습니다. ");
            return ResponseEntity.ok(response);
        }

        log.warn("password does not match..");
        ResponseDto response = new ResponseDto();
        response.setMessage("비밀번호와 비밀번호 확인이 틀립니다.");
        return ResponseEntity.badRequest().body(response);

    }


}
