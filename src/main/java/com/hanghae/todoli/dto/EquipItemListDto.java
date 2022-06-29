package com.hanghae.todoli.dto;

import com.hanghae.todoli.models.Category;
import lombok.*;

//장착한 아이템 리스트
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class EquipItemListDto {
    private Long itemId;
    private String equipImg;
    private Category category;
}
