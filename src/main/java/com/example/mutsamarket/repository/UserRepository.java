package com.example.mutsamarket.repository;

import com.example.mutsamarket.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

}
