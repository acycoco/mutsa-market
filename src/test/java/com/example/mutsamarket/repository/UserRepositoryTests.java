package com.example.mutsamarket.repository;

import com.example.mutsamarket.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
    public void createUser(){
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


}
