package com.hanghae.todoli.models;

import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Character {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String charImg;

    @Column
    private int maxHp = 100;

    @Column
    private int hp = 100;

    @Column
    private int maxExp = 100;

    @Column
    private int exp = 0;

    @Column
    private int level = 1;

    @Column
    private int money = 10;

    @OneToMany
    private List<Inventory> inventory = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    private EquipItem equipItem;
}
