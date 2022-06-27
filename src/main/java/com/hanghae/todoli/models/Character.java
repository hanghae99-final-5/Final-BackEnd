package com.hanghae.todoli.models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Character {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String charImg;

    @Column
    private int maxHp;

    @Column
    private int hp;

    @Column
    private int maxExp;

    @Column
    private int exp;

    @Column
    private int level;

    @Column
    private int money;

    @OneToMany
    private List<Inventory> inventory = new ArrayList<>();
}