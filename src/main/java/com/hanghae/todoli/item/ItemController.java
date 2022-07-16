package com.hanghae.todoli.item;

import com.hanghae.todoli.equipitem.EquipItemDto;
import com.hanghae.todoli.item.Dto.ExistItemListDto;
import com.hanghae.todoli.item.Dto.ItemRequestDto;
import com.hanghae.todoli.item.Dto.ItemResponseDto;
import com.hanghae.todoli.security.UserDetailsImpl;
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
    public List<ExistItemListDto> getExistItemList(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return itemService.getExistItemList(userDetails);
    }

    //상점 아이템 목록 조회
    @GetMapping("/api/items")
    public ItemResponseDto getShopItemList(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return itemService.getShopItemList(userDetails);
    }

    //아이템 구매
    @PostMapping("/api/items/{itemId}")
    public String buyItem(@PathVariable Long itemId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        itemService.buyItem(itemId, userDetails);
        return "구매를 성공하였습니다.";
    }

    //아이템 장착
    @PatchMapping("/api/items/{itemId}")
    public EquipItemDto equipItem(@PathVariable Long itemId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return itemService.equipItem(itemId, userDetails);
    }

    //아이템 등록(h2에서만 사용)
    @PostMapping("/api/items")
    public String inputItem(@RequestBody ItemRequestDto itemRequestDto) {
        itemService.inputItem(itemRequestDto);
        return "등록을 성공하였습니다.";
    }
}