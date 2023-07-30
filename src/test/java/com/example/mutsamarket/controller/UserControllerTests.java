package com.example.mutsamarket.controller;

import com.example.mutsamarket.dto.ResponseDto;
import com.example.mutsamarket.entity.CustomUserDetails;
import com.example.mutsamarket.entity.UserEntity;
import com.example.mutsamarket.service.JpaUserDetailsManager;
import com.example.mutsamarket.service.JpaUserDetailsManagerTests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(MockitoExtension.class)
public class UserControllerTests {
    @Mock
    private UserDetailsManager manager;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserController userController;
    private MockMvc mockMvc;

    @BeforeEach
    public void init(){
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void testRegisterUser() throws Exception {
        //given
        String username = "user";
        String password = "1234";
        String passwordCheck = "1234";

        ResponseDto response = new ResponseDto();
        response.setMessage("회원가입이 완료되었습니다. ");

        //when
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");

        //then
        ResultActions result = mockMvc.perform(post("/users/register")
                .param("username", username)
                .param("password", password)
                .param("password-check", passwordCheck));

        result.andExpectAll(
                status().is2xxSuccessful(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.message").value("회원가입이 완료되었습니다. "));

        verify(manager, times(1)).createUser(any());

    }
}
