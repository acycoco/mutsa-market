package com.example.mutsamarket.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;
    //TODO validation 추가
    @Column(nullable = false)
    private String password;

    private String phone;
    private String email;
    private String address;
}
