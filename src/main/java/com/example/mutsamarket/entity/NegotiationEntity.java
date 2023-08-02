package com.example.mutsamarket.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "negotiation")
public class NegotiationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer suggestedPrice;

    private String status;

    private String writer;

    private String password;

    @ManyToOne
    private ItemEntity item;

    @ManyToOne
    private UserEntity user;
}
