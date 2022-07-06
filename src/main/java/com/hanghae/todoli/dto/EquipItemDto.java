package com.hanghae.todoli.dto;

import com.hanghae.todoli.models.Category;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Builder
public class EquipItemDto {
    private Long itemId;
    private String equipImg;
    private Category category;
}
