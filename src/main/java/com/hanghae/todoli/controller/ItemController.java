package com.hanghae.todoli.controller;

import com.hanghae.todoli.dto.EquipItemDto;
import com.hanghae.todoli.dto.ExistItemListDto;
import com.hanghae.todoli.dto.ItemRequestDto;
import com.hanghae.todoli.dto.ItemResponseDto;
import com.hanghae.todoli.security.jwt.UserDetailsImpl;
import com.hanghae.todoli.service.ItemService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ItemController {

    private final ItemService itemService;

    //가지고있는 아이템들 조회
    @ApiResponses({
            @ApiResponse(code=200, message="조회 성공"),
            @ApiResponse(code=400, message="실패"),
            @ApiResponse(code=403, message="Forbidden")
    })
    @ApiOperation(value = "아이템 조회 메소드", notes = "자신의 아이템 조회 api 입니다.")
    @GetMapping("/api/inventories")
    public List<ExistItemListDto> getExistItemList(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return itemService.getExistItemList(userDetails);
    }

    //상점 아이템 목록 조회
    @ApiResponses({
            @ApiResponse(code=200, message="조회 성공"),
            @ApiResponse(code=400, message="실패"),
            @ApiResponse(code=403, message="Forbidden")
    })
    @ApiOperation(value = "상점 조회 메소드", notes = "상점 아이템 조회 api 입니다.")
    @GetMapping("/api/items")
    public ItemResponseDto getShopItemList(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return itemService.getShopItemList(userDetails);
    }

    //아이템 구매
    @ApiResponses({
            @ApiResponse(code=200, message="구매 성공"),
            @ApiResponse(code=400, message="실패"),
            @ApiResponse(code=403, message="Forbidden")
    })
    @ApiOperation(value = "아이템 구매 메소드", notes = "아이템 구매 api 입니다.")
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
    @ApiResponses({
            @ApiResponse(code=200, message="장착 성공"),
            @ApiResponse(code=400, message="실패"),
            @ApiResponse(code=403, message="Forbidden")
    })
    @ApiOperation(value = "아이템 장착 메소드", notes = "구매한 아이템을 장착하는 api 입니다.")
    @PatchMapping("/api/items/{itemId}")
    public EquipItemDto equipItem(@PathVariable Long itemId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return itemService.equipItem(itemId, userDetails);
    }

    //아이템 등록(h2에서만 사용)
    @ApiResponses({
            @ApiResponse(code=200, message="등록 성공"),
            @ApiResponse(code=400, message="실패"),
            @ApiResponse(code=403, message="Forbidden")
    })
    @ApiOperation(value = "아이템 등록 메소드", notes = "아이템 등록 api 입니다.")
    @PostMapping("/api/items")
    public String inputItem(@RequestBody ItemRequestDto itemRequestDto){
        itemService.inputItem(itemRequestDto);
        return "등록을 성공하였습니다.";
    }
}