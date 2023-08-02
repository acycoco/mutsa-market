package com.example.mutsamarket.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "users")
@Data
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //UserEntity의 username과 password만 not null
    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String phone;
    private String email;
    private String address;

    @OneToMany(mappedBy = "user")
    private List<ItemEntity> items;

    @OneToMany(mappedBy = "user")
    private List<CommentEntity> comments;

    @OneToMany(mappedBy = "user")
    private List<NegotiationEntity> negotiations;

}
