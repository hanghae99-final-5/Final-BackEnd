package com.hanghae.todoli.models;

import lombok.Getter;
import org.hibernate.annotations.GeneratorType;

import javax.persistence.*;

@Entity
@Getter
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
