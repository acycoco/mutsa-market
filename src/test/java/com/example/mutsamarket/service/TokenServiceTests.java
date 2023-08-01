package com.example.mutsamarket.service;

import com.example.mutsamarket.dto.jwt.JwtRequestDto;
import com.example.mutsamarket.dto.jwt.JwtTokenDto;
import com.example.mutsamarket.entity.CustomUserDetails;
import com.example.mutsamarket.entity.UserEntity;
import com.example.mutsamarket.jwt.JwtTokenUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TokenServiceTests {
    @Mock
    private UserDetailsManager manager;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenUtils jwtTokenUtils;
    @InjectMocks
    private TokenService tokenService;


    @Test
    public void testIssueJwtSuccess(){
        //given
        String username = "user";
        String password = "1234";
        String encodedPassword = "encodedPassword";
        String generatedToken = "generatedToken";

        //파라미터 값
        JwtRequestDto dto = new JwtRequestDto();
        dto.setUsername(username);
        dto.setPassword(password);

        //manager.loadUserByUsername()의 반환값
        UserDetails userDetails = CustomUserDetails.builder()
                .username(username)
                .password(encodedPassword)
                .build();



        //Mock개체 동작 가정
        when(manager.loadUserByUsername(dto.getUsername()))
                .thenReturn(userDetails);

        when(passwordEncoder.matches(dto.getPassword(), userDetails.getPassword()))
                .thenReturn(true);

        when(jwtTokenUtils.generateToken(userDetails))
                .thenReturn(generatedToken);

        //반환값 설정
        JwtTokenDto jwtTokenDto = new JwtTokenDto();
        jwtTokenDto.setToken(generatedToken);

        //when
        JwtTokenDto result = tokenService.issueJwt(dto);

        //then
        assertThat(result).isNotNull();
        assertEquals(jwtTokenDto, result);

        verify(manager, times(1)).loadUserByUsername(dto.getUsername());
        verify(passwordEncoder, times(1)).matches(dto.getPassword(), encodedPassword);
        verify(jwtTokenUtils, times(1)).generateToken(userDetails);


    }

    @Test
    public void testIssueJwtFail(){
        //given
        String username = "user";
        String password = "1234";
        String encodedPassword = "encodedPassword";
        String generatedToken = "generatedToken";

        //파라미터 값
        JwtRequestDto dto = new JwtRequestDto();
        dto.setUsername(username);
        dto.setPassword(password);

        //manager.loadUserByUsername()의 반환값
        UserDetails userDetails = CustomUserDetails.builder()
                .username(username)
                .password(encodedPassword)
                .build();


        //Mock 개체 동작 설정
        when(manager.loadUserByUsername(dto.getUsername()))
                .thenReturn(userDetails);

        when(passwordEncoder.matches(dto.getPassword(), userDetails.getPassword()))
                .thenReturn(false);


        //when
        //then
        assertThrows(ResponseStatusException.class, () -> {
            tokenService.issueJwt(dto);
        });

        verify(manager, times(1)).loadUserByUsername(dto.getUsername());
        verify(passwordEncoder, times(1)).matches(dto.getPassword(), encodedPassword);

    }
}
