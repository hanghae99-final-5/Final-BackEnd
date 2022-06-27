package com.hanghae.todoli.models;

import javax.persistence.*;

@Entity
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private Category category;

    @Column
    private String equipImg;

    @Column
    private String viewImg;

    @Column
    private int price;
}
