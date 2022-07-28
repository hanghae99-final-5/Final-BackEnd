package com.hanghae.todoli.character.Dto;

import com.hanghae.todoli.item.Category;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class ThumbnailDto {
    private Long itemId;
    private String thumbnailImg;
    private Category category;

    @QueryProjection
    public ThumbnailDto(Long itemId, String thumbnailImg, Category category) {
        this.itemId = itemId;
        this.thumbnailImg = thumbnailImg;
        this.category = category;
    }
}
