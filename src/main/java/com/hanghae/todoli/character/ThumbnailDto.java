package com.hanghae.todoli.character;

import com.hanghae.todoli.item.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ThumbnailDto {
    private Long itemId;
    private String thumbnailImg;
    private Category category;
}
