package com.hanghae.todoli.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ItemResponseDto {
    private List<ExistItemListDto.ExistInventoriesDto> inventories;    //itemId

    private List<ExistItemListDto.ItemListDto> items;   //itemId, name, viewImg, catrgory, price
}
