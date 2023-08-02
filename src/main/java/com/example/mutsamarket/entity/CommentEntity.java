package com.example.mutsamarket.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "comment")
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String writer;

    private String password;

    @Column(nullable = false)
    private String content;

    private String reply;

    @ManyToOne
    private ItemEntity item;

    @ManyToOne
    private UserEntity user;
}
