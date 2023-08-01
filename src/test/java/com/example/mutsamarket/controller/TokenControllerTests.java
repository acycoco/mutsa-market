package com.example.mutsamarket.controller;

import com.example.mutsamarket.JsonUtil;
import com.example.mutsamarket.dto.jwt.JwtRequestDto;
import com.example.mutsamarket.dto.jwt.JwtTokenDto;
import com.example.mutsamarket.service.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@ExtendWith(MockitoExtension.class)
public class TokenControllerTests {
    @Mock
    private TokenService tokenService;
    @InjectMocks
    private TokenController tokenController;

    private MockMvc mockMvc;
    @BeforeEach
    public void init(){
        mockMvc = MockMvcBuilders.standaloneSetup(tokenController).build();
    }

    @Test
    @DisplayName("issue token /token/issue")
    public void testIssue() throws Exception {
        //given
        String username = "user";
        String password = "1234";

        //parameter준비
        JwtRequestDto dto = new JwtRequestDto();
        dto.setUsername(username);
        dto.setPassword(password);

        //반환값 준비
        String generatedToken = "generatedToken";
        JwtTokenDto jwtTokenDto = new JwtTokenDto();
        jwtTokenDto.setToken(generatedToken);

        when(tokenService.issueJwt(dto)).thenReturn(jwtTokenDto);

        //when
        //then
        mockMvc.perform(
                post("/token/issue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(dto)))

                .andExpectAll(
                        status().is2xxSuccessful(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.token").value(generatedToken));


        verify(tokenService, times(1)).issueJwt(dto);
    }

}
