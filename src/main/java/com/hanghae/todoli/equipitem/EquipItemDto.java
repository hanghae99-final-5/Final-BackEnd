package com.hanghae.todoli.equipitem;

import com.hanghae.todoli.item.Category;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class EquipItemDto {
    private Long itemId;
    private String equipImg;
    private Category category;
}
