package com.hanghae.todoli.service;

import com.hanghae.todoli.dto.EquipItemDto;
import com.hanghae.todoli.dto.ExistItemListDto;
import com.hanghae.todoli.dto.ItemResponseDto;
import com.hanghae.todoli.models.*;
import com.hanghae.todoli.models.Character;
import com.hanghae.todoli.repository.CharacterRepository;
import com.hanghae.todoli.repository.EquipItemRepository;
import com.hanghae.todoli.repository.InventoryRepository;
import com.hanghae.todoli.repository.ItemRepository;
import com.hanghae.todoli.security.jwt.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ItemService {

    private final ItemRepository itemRepository;
    private final InventoryRepository inventoryRepository;

    private final CharacterRepository characterRepository;
    private final EquipItemRepository equipItemRepository;

    //가지고있는 아이템들 조회
    @Transactional
    public List<ExistItemListDto> getExistItemList(UserDetailsImpl userDetails) {
        Character c = userDetails.getMember().getCharacter();
        return existItemList(c);
    }


    //상점 아이템 목록 조회
    @Transactional
    public ItemResponseDto getShopItemList(UserDetailsImpl userDetails) {

        List<ExistItemListDto.ExistInventoriesDto> existItemIdList = new ArrayList<>();
        List<ExistItemListDto.ItemListDto> AllItemList = new ArrayList<>();

        //1. 가지고 있는 ItemId 조회
        List<Inventory> inventory = userDetails.getMember().getCharacter().getInventory();
        for(Inventory i : inventory){
            Long itemId = i.getItem().getId();
            ExistItemListDto.ExistInventoriesDto  existInventoriesDto = ExistItemListDto.ExistInventoriesDto.builder()
                    .itemId(itemId)
                    .build();
            existItemIdList.add(existInventoriesDto);
        }

        //2. 상점에 있는 모든 Item 정보 조회
        List<Item> allItem = itemRepository.findAll();
        for(Item item : allItem){
            ExistItemListDto.ItemListDto itemListDto = ExistItemListDto.ItemListDto.builder()
                    .itemId(item.getId())
                    .name(item.getName())
                    .viewImg(item.getViewImg())
                    .category(item.getCategory())
                    .price(item.getPrice())
                    .build();
            AllItemList.add(itemListDto);
        }
        return new ItemResponseDto(existItemIdList, AllItemList);
    }


    //가지고 있는 아이템의 정보들을 리턴(가지고 있는 아이템 조회에서 쓰인다.)
    private List<ExistItemListDto> existItemList(Character c) {
        List<ExistItemListDto> existItemList = new ArrayList<>();
        List<Inventory> inventory = c.getInventory();
        for(Inventory i : inventory){

            Item item = i.getItem();

            Long itemId = item.getId();
            String name = item.getName();
            String viewImg = item.getViewImg();
            Category category = item.getCategory();

            ExistItemListDto existItemListDto = ExistItemListDto.builder()
                    .itemId(itemId)
                    .name(name)
                    .viewImg(viewImg)
                    .category(category)
                    .build();
            existItemList.add(existItemListDto);
        }
        return existItemList;
    }


    //아이템 구매
    @Transactional
    public void buyItem(Long itemId, UserDetailsImpl userDetails) {
        Item buyItem = findItem(itemId);
        Character character = userDetails.getMember().getCharacter();

        //구매했다면
        Inventory exist = inventoryRepository.findByCharacterAndItem(character, buyItem).orElse(null);

        if(exist == null){
            //계산
            if(character.getMoney() >= buyItem.getPrice()){
                character.minMoney(buyItem.getPrice());     //charRepository에 저장해야하나???
                characterRepository.save(character);
            }
            else
                throw new IllegalArgumentException("잔액이 부족합니다.");

            Inventory inventory = new Inventory(buyItem, character);
            inventoryRepository.save(inventory);
        }
        else
            throw new IllegalArgumentException("이미 구매하신 물품입니다.");

    }

    private Item findItem(Long itemId) {
        Item buyItem = itemRepository.findById(itemId).orElseThrow(
                () -> new IllegalArgumentException("아이템이 존재하지 않습니다.")
        );
        return buyItem;
    }


    //아이템 장착                equipItem에 접근해서 patch.(switch문)
    @Transactional
    public EquipItemDto equipItem(Long itemId, UserDetailsImpl userDetails) {
        Character character = userDetails.getMember().getCharacter();
        Item item = findItem(itemId);

        Optional<Inventory> found = inventoryRepository.findByCharacterAndItem(character, item);
        if(found.isEmpty())
            throw new IllegalArgumentException("아이템을 먼저 구매해 주세요.");

        EquipItem equipItem = character.getEquipItem();
        Category category = item.getCategory();
        EquipItemDto equipItemDto = getEquipItemDto(item);

        switch(category){
            case HAT:
                equipItem.updateHat(itemId);
            case ACCESSORY:
                equipItem.updateAccessory(itemId);
            case HAIR:
                equipItem.updateHair(itemId);
            case CLOTH:
                equipItem.updateCloth(itemId);
        }
        equipItemRepository.save(equipItem);          //save 안해줘도 되나?
        return equipItemDto;
    }

    //장착하는 아이템에 대한 정보를 가져온다.
    private EquipItemDto getEquipItemDto(Item item) {
        EquipItemDto equipItemDto = EquipItemDto.builder()
                .itemId(item.getId())
                .equipImg(item.getEquipImg())
                .category(item.getCategory())
                .build();
        return equipItemDto;
    }


}