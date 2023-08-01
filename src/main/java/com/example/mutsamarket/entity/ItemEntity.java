package com.example.mutsamarket.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "sales_item")
public class ItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    private String imageUrl;

    @Column(nullable = false)
    private Integer minPriceWanted;

    private String status;

    @Column(nullable = false)
    private String writer;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "item")
    private List<CommentEntity> comments;

    @OneToMany(mappedBy = "item")
    private List<NegotiationEntity> negotiations;

}
