package com.example.mutsamarket.controller;

import com.example.mutsamarket.dto.jwt.JwtRequestDto;
import com.example.mutsamarket.dto.jwt.JwtTokenDto;
import com.example.mutsamarket.service.TokenService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/token")
public class TokenController {
    private final TokenService tokenService;

    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    //jwt 발급
    @PostMapping("/issue")
    public ResponseEntity<JwtTokenDto> issue(
            @Valid @RequestBody JwtRequestDto dto
    ){
        JwtTokenDto response = tokenService.issueJwt(dto);

        return ResponseEntity.ok(response);
    }

}
