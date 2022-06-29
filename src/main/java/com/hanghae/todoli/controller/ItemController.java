package com.hanghae.todoli.controller;

import com.hanghae.todoli.dto.EquipItemDto;
import com.hanghae.todoli.dto.ExistItemListDto;
import com.hanghae.todoli.dto.ItemResponseDto;
import com.hanghae.todoli.security.jwt.UserDetailsImpl;
import com.hanghae.todoli.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ItemController {

    private final ItemService itemService;

    //가지고있는 아이템들 조회
    @GetMapping("/api/inventories")
    public List<ExistItemListDto> getExistItemList(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return itemService.getExistItemList(userDetails);
    }

    //상점 아이템 목록 조회
    @GetMapping("/api/Items")
    public ItemResponseDto getShopItemList(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return itemService.getShopItemList(userDetails);
    }

    //아이템 구매
    @PostMapping("/api/items/{itemId}")
    public String buyItem(@PathVariable Long itemId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        try{
            itemService.buyItem(itemId, userDetails);
            return "성공적으로 구매하였습니다.";
        }catch (IllegalArgumentException e){
            return e.getMessage();
        }
    }

    //아이템 장착
    @PatchMapping("/api/items/{itemId}")
    public EquipItemDto equipItem(@PathVariable Long itemId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return itemService.equipItem(itemId, userDetails);
    }

    //아이템 등록

}