package com.example.mutsamarket.entity;

import jakarta.persistence.*;
import lombok.Data;

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

    @Column(name = "min_price_wanted", nullable = false)
    private Integer minPrice;

    private String status;

    @Column(nullable = false)
    private String writer;

    @Column(nullable = false)
    private String password;

}
