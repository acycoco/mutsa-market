package com.example.mutsamarket.service;

import com.example.mutsamarket.entity.CustomUserDetails;
import com.example.mutsamarket.entity.UserEntity;
import com.example.mutsamarket.repository.UserRepository;
import com.example.mutsamarket.service.JpaUserDetailsManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class JpaUserDetailsManagerTests {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private JpaUserDetailsManager manager;

    @Test
    public void testLoadByUsername(){
        //given
        String username1 = "user";
        String username2 = "user2"; // 저장 안할 것
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername(username1);
        user.setPassword("1234");

        //when
        when(userRepository.findByUsername(username1))
                .thenReturn(Optional.of(user));
        when(userRepository.findByUsername(username2))
                .thenReturn(Optional.empty());
        UserDetails result1 = manager.loadUserByUsername(username1);

        //then
        assertThat(result1).isNotNull();
        assertEquals(username1, result1.getUsername());
        assertThrows(UsernameNotFoundException.class, () -> {
            manager.loadUserByUsername(username2); //없는 user
        });
    }

    @Test
    public void testCreateUserFail(){
        //given
        String username = "user";
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername(username);
        user.setPassword("1234");
        UserDetails userDetails = User.withUsername(username).password("1234").build();

        //when
        when(userRepository.existsByUsername(username)).thenReturn(true);

        //then
        //중복 username create 시
        assertThrows(ResponseStatusException.class, () -> {
            manager.createUser(userDetails);
        });
    }

    @Test
    public void testCreateUserSuccess(){
        //given
        String username = "user";
        String password = "1234";
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername(username);
        user.setPassword(password);
        CustomUserDetails userDetails = CustomUserDetails.fromEntity(user);

        //when
        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(userRepository.save(any(UserEntity.class)))
                .thenReturn(user);
        manager.createUser(userDetails);

        //then
        verify(userRepository).save(any(UserEntity.class)); //save메소드가 호출되었는지 검증

    }

    @Test
    public void testUserExists(){
        //given
        String username1 = "user";
        String username2 = "user2";

        //when
        when(userRepository.existsByUsername(username1)).thenReturn(true);
        when(userRepository.existsByUsername(username2)).thenReturn(false);

        Boolean result1 = manager.userExists(username1);
        Boolean result2 = manager.userExists(username2);

        //then
        assertTrue(result1);
        assertFalse(result2);
    }

    @Test
    public void testUpdateUserSuccess(){
        //given
        String username = "user";
        String password = "1234";

        CustomUserDetails userDetails = CustomUserDetails.builder()
                .username(username)
                .password(password)
                .build();


        when(userRepository.findByUsername(userDetails.getUsername()))
                .thenReturn(Optional.of(userDetails.newEntity()));

        //when
        manager.updateUser(userDetails);

        //then
        verify(userRepository, times(1)).save(any(UserEntity.class));

    }

    @Test
    public void testUpdateUserFail(){
        //given
        String username = "user";
        String password = "1234";

        CustomUserDetails userDetails = CustomUserDetails.builder()
                .username(username)
                .password(password)
                .build();


        when(userRepository.findByUsername(userDetails.getUsername()))
                .thenReturn(Optional.empty());

        //when
        assertThrows(ResponseStatusException.class, () -> {

            manager.updateUser(userDetails);
        });

        //then
        verify(userRepository, never()).save(any(UserEntity.class));

    }
}
