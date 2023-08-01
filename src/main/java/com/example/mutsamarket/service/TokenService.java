package com.example.mutsamarket.service;

import com.example.mutsamarket.dto.jwt.JwtRequestDto;
import com.example.mutsamarket.dto.jwt.JwtTokenDto;
import com.example.mutsamarket.jwt.JwtTokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
public class TokenService {
    private final UserDetailsManager manager;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtils;

    public TokenService(UserDetailsManager manager, PasswordEncoder passwordEncoder, JwtTokenUtils jwtTokenUtils) {
        this.manager = manager;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtils = jwtTokenUtils;
    }

    //jwt 발급
    public JwtTokenDto issueJwt(JwtRequestDto dto){
        //사용자 정보 가져오기
        UserDetails userDetails = manager.loadUserByUsername(dto.getUsername());
        //비밀번호가 일치하지 않을 때
        if (!passwordEncoder.matches(dto.getPassword(), userDetails.getPassword())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        JwtTokenDto jwtTokenDto = new JwtTokenDto();
        jwtTokenDto.setToken(jwtTokenUtils.generateToken(userDetails));

        return jwtTokenDto;
    }
}
