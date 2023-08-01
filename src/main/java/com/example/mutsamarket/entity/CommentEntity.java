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

    @Column(nullable = false)
    private String writer;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String content;

    private String reply;

    @ManyToOne
    private ItemEntity item;
}
