package com.hanghae.todoli.models;

import com.hanghae.todoli.dto.ItemRequestDto;
import lombok.Getter;

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

    public Item(ItemRequestDto itemRequestDto) {
        this.name = itemRequestDto.getName();
        this.category = itemRequestDto.getCategory();
        this.equipImg = itemRequestDto.getEquipImg();
        this.viewImg = itemRequestDto.getViewImg();
        this.price = itemRequestDto.getPrice();
    }
}
