package com.hanghae.todoli.dto;

import com.hanghae.todoli.models.Category;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Builder
public class ItemRequestDto {
    private String name;
    private Category category;
    private String equipImg;
    private String viewImg;
    private int price;
}
