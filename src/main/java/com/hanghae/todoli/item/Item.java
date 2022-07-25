package com.hanghae.todoli.item;

import com.hanghae.todoli.item.Dto.ItemRequestDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Enumerated(EnumType.STRING)
    @Column
    private Category category;

    @Column
    private String equipImg;

    @Column
    private String viewImg;

    @Column
    private String thumbnailImg;

    @Column
    private int price;

    public Item(ItemRequestDto itemRequestDto) {
        this.name = itemRequestDto.getName();
        this.category = itemRequestDto.getCategory();
        this.equipImg = itemRequestDto.getEquipImg();
        this.thumbnailImg = itemRequestDto.getThumbnailImg();
        this.viewImg = itemRequestDto.getViewImg();
        this.price = itemRequestDto.getPrice();
    }
}
