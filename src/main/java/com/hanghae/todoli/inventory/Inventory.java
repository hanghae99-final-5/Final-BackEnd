package com.hanghae.todoli.inventory;

import com.hanghae.todoli.character.Character;
import com.hanghae.todoli.item.Item;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor

public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    private Character character;


    public Inventory(Item buyItem, Character character) {
        this.item = buyItem;
        this.character = character;
    }
}