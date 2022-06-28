package com.hanghae.todoli.models;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Item item;
}