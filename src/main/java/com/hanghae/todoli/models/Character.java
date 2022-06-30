package com.hanghae.todoli.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
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

    @JsonManagedReference
    @OneToMany(mappedBy = "character", orphanRemoval = true)
    private List<Inventory> inventory = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    private EquipItem equipItem;


    public void minMoney(int price) {
        this.money -= price;
    }

    public Character(EquipItem equipItem) {
        this.equipItem = equipItem;
        this.charImg = "charImg";
        this.maxHp = 100;
        this.hp = 100;
        this.maxExp = 100;
        this.exp = 0;
        this.level = 1;
        this.money = 10000;

    }
}
