package com.hanghae.todoli.character;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hanghae.todoli.equipitem.EquipItem;
import com.hanghae.todoli.inventory.Inventory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Characters")
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
    @OneToMany(mappedBy = "character", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Inventory> inventory = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private EquipItem equipItem;


    public Character(EquipItem equipItem) {
        this.equipItem = equipItem;
        this.charImg = new CharacterImg().getCharImg();
        this.maxHp = 100;
        this.hp = 100;
        this.maxExp = 100;
        this.exp = 0;
        this.level = 1;
        this.money = 10000;
    }

    public void minMoney(int price) {
        this.money -= price;
    }

    public void setMoney(int money) {
        this.money += money;
    }

    public void editExp(int exp) {
        this.exp += exp;
        if (this.exp >= 100) {
            this.level++;
            this.exp = this.exp - 100;
            this.hp = 100;
        }
    }

    public void minHpAndLv() {
        this.hp -= 10;

        //0 아래로 내려가거나, 레벨이 1초과인 경우만 레벨을 뺀 후 hp를 100으로 돌려놓는다.
        if (this.level > 1 && hp <= 0) {
            this.level--;
            this.hp = 100;
        }

        //음수 방지
        if (hp < 0) {
            hp = 0;
        }
    }


}
