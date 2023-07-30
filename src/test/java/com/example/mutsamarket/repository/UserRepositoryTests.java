package com.example.mutsamarket.repository;

import com.example.mutsamarket.entity.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
public class UserRepositoryTests {
    @Autowired
    private UserRepository userRepository;

    @Test
    //UserRepository가 잘 생성되었는지 테스트
    public void UserRepositoryIsNotNull(){
        assertThat(userRepository).isNotNull();
    }

    @Test
    @DisplayName("create userEntity")
    public void testCreateUser(){
        //given
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("user");
        userEntity.setPassword("1234");

        //when
        userEntity = userRepository.save(userEntity);

        //then
        assertThat(userEntity).isNotNull();
        assertThat(userEntity.getId()).isNotNull();
        assertEquals(userEntity.getUsername(),"user");
        assertEquals(userEntity.getPassword(),"1234");
    }

    @Test
    @DisplayName("username unique 제약사항")
    //username unique제약사항 test
    public void testUsernameUnique(){
        //given
        String username = "user";
        UserEntity user1 = new UserEntity();
        user1.setUsername(username);
        user1.setPassword("1234");
        userRepository.save(user1);

        UserEntity user2 = new UserEntity();
        user2.setUsername(username);
        user2.setPassword("1234");

        //when
        //then
        assertThrows(Exception.class, () -> {
            userRepository.save(user2);
        });
    }

    @Test
    @DisplayName("username not null")
    public void testUsernameNotNull(){
        //given
        UserEntity user = new UserEntity();
        user.setUsername(null);
        user.setPassword("1234");

        //when
        //then
        assertThrows(Exception.class, () -> {
            userRepository.save(user);
        });
    }

    @Test
    @DisplayName("password not null")
    public void testPasswordNotNull(){
        //given
        UserEntity user = new UserEntity();
        user.setUsername("user");
        user.setPassword(null);

        //when
        //then
        assertThrows(Exception.class, () -> {
            userRepository.save(user);
        });
    }


    @Test
    @DisplayName("username으로 UserEntity 찾기")
    public void testFindByUsername(){
        //given
        String username = "user";
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword("1234");
        userRepository.save(user);


        //when
        Optional<UserEntity> optionalUser = userRepository.findByUsername("user");

        //then
        assertTrue(optionalUser.isPresent());
        assertEquals(username, optionalUser.get().getUsername());
    }

    @Test
    @DisplayName("username으로 존재하는지 확인")
    public void testExitsByUsername(){
        //given
        UserEntity user = new UserEntity();
        user.setUsername("user");
        user.setPassword("1234");
        userRepository.save(user);


        //when
        Boolean result1 = userRepository.existsByUsername("user");
        Boolean result2 = userRepository.existsByUsername("user1");

        //then
        assertTrue(result1);
        assertFalse(result2);
    }

}
