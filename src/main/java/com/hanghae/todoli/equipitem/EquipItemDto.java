package com.hanghae.todoli.equipitem;

import com.hanghae.todoli.item.Category;
import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@Builder
public class EquipItemDto {
    private Long itemId;
    private String equipImg;
    private Category category;

    @QueryProjection
    public EquipItemDto(Long itemId, String equipImg, Category category) {
        this.itemId = itemId;
        this.equipImg = equipImg;
        this.category = category;
    }
}
