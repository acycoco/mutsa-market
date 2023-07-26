package com.example.mutsamarket.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "user")
@Data
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;
    //TODO validation 추가
    private String password;
    private String phone;
    private String email;
    private String address;
}
