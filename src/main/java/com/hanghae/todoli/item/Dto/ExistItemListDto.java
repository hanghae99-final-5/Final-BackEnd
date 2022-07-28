package com.hanghae.todoli.item.Dto;

import com.hanghae.todoli.item.Category;
import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

@NoArgsConstructor
//@AllArgsConstructor
@Getter
@Setter
@Builder
public class ExistItemListDto {
    //1. 가지고 있는 아이템 조회
    private Long itemId;
    private String name;
    private String viewImg;
    private Category category;

    @QueryProjection
    public ExistItemListDto(Long itemId, String name, String viewImg, Category category) {
        this.itemId = itemId;
        this.name = name;
        this.viewImg = viewImg;
        this.category = category;
    }

//2. 상점 아이템 목록 조회

    //2-1 현재 가지고 있는 ItemId
    @Data
    @Builder
    public static class ExistInventoriesDto {
        private Long itemId;
    }

    //2-2 전체 Item List
    @Data
    @Builder
    public static class ItemListDto {
        private Long itemId;
        private String name;
        private String viewImg;
        private Category category;
        private int price;
    }
}


