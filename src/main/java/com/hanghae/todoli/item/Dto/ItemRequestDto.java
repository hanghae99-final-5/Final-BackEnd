package com.hanghae.todoli.item.Dto;

import com.hanghae.todoli.item.Category;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ItemRequestDto {
    private String name;
    private Category category;
    private String equipImg;
    private String viewImg;
    private String thumbnailImg;
    private int price;
}
