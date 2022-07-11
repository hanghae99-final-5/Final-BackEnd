package com.hanghae.todoli.item;

import com.hanghae.todoli.character.Character;
import com.hanghae.todoli.character.CharacterRepository;
import com.hanghae.todoli.equipitem.EquipItem;
import com.hanghae.todoli.equipitem.EquipItemDto;
import com.hanghae.todoli.equipitem.EquipItemRepository;
import com.hanghae.todoli.exception.CustomException;
import com.hanghae.todoli.exception.ErrorCode;
import com.hanghae.todoli.inventory.Inventory;
import com.hanghae.todoli.inventory.InventoryRepository;
import com.hanghae.todoli.member.Member;
import com.hanghae.todoli.member.MemberRepository;
import com.hanghae.todoli.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final MemberRepository memberRepository;

    //가지고있는 아이템들 조회
    @Transactional
    public List<ExistItemListDto> getExistItemList(UserDetailsImpl userDetails) {
        Long memberId = userDetails.getMember().getId();
        Member member = getMember(memberId);
        Character character = member.getCharacter();
        return existItemList(character);
    }

    //가지고 있는 아이템의 정보들을 리턴(가지고 있는 아이템 조회에서 쓰인다.)
    private List<ExistItemListDto> existItemList(Character c) {
        List<ExistItemListDto> existItemList = new ArrayList<>();
        List<Inventory> inventory = c.getInventory();
        for (Inventory i : inventory) {

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

    //상점 아이템 목록 조회
    @Transactional
    public ItemResponseDto getShopItemList(UserDetailsImpl userDetails) {
        List<ExistItemListDto.ExistInventoriesDto> existItemIdList = new ArrayList<>();
        List<ExistItemListDto.ItemListDto> AllItemList = new ArrayList<>();

        //1. 가지고 있는 ItemId 조회
        Long memberId = userDetails.getMember().getId();
        Member member = getMember(memberId);

        List<Inventory> getInventory = member.getCharacter().getInventory();

        for (Inventory inventory : getInventory) {
            Long itemId = inventory.getItem().getId();
            ExistItemListDto.ExistInventoriesDto existInventoriesDto = ExistItemListDto.ExistInventoriesDto.builder()
                    .itemId(itemId)
                    .build();
            existItemIdList.add(existInventoriesDto);
        }

        //2. 상점에 있는 모든 Item 정보 조회
        List<Item> allItem = itemRepository.findAllExceptBasic();
        for (Item item : allItem) {
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


    //아이템 구매
    @Transactional
    public void buyItem(Long itemId, UserDetailsImpl userDetails) {
        Item buyItem = findItem(itemId);

        Long memberId = userDetails.getMember().getId();
        Member member = getMember(memberId);

        Character character = member.getCharacter();

        //구매했다면
        Inventory exist = inventoryRepository.findByCharacterAndItem(character, buyItem).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_ITEM)
        );

        if (exist == null) {
            //계산
            if (character.getMoney() >= buyItem.getPrice()) {
                character.minMoney(buyItem.getPrice());     //charRepository에 저장해야하나???
                characterRepository.save(character);
            } else {
                throw new CustomException(ErrorCode.NOT_ENOUGH_MONEY);
            }

            Inventory inventory = new Inventory(buyItem, character);
            inventoryRepository.save(inventory);

        } else {
            throw new CustomException(ErrorCode.ALREADY_GOT_ITEM);
        }


    }

    //아이템 장착
    @Transactional
    public EquipItemDto equipItem(Long itemId, UserDetailsImpl userDetails) {
        Long memberId = userDetails.getMember().getId();
        Member member = getMember(memberId);
        Character character = member.getCharacter();
        Item item = findItem(itemId);

        Optional<Inventory> found = inventoryRepository.findByCharacterAndItem(character, item);
        if (found.isEmpty())
            throw new CustomException(ErrorCode.NOT_FOUND_ITEM);

        EquipItem equipItem = character.getEquipItem();
        Category category = item.getCategory();
        
        switch (category) {
            case HAIR:
                equipItem.updateHair(itemId);
                break;
            case ACCESSORY:
                equipItem.updateAccessory(itemId);
                break;
            case CLOTH:
                equipItem.updateCloth(itemId);
                break;
        }
        equipItemRepository.save(equipItem);          //save 안해줘도 되나?

        return getEquipItemDto(item);
    }

    //아이템 찾기
    private Item findItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(
                () -> new CustomException(ErrorCode.NO_ITEM));
    }

    //장착하는 아이템에 대한 정보를 가져온다.
    private EquipItemDto getEquipItemDto(Item item) {
        return EquipItemDto.builder()
                .itemId(item.getId())
                .equipImg(item.getEquipImg())
                .category(item.getCategory())
                .build();
    }


    // 아이템 등록
    @Transactional
    public void inputItem(ItemRequestDto requestDto) {
        String name = requestDto.getName();
        Category category = requestDto.getCategory();
        String equipImg = requestDto.getEquipImg();
        String thumbnailImg = requestDto.getThumbnailImg();
        String viewImg = requestDto.getViewImg();
        int price = requestDto.getPrice();

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .name(name)
                .category(category)
                .equipImg(equipImg)
                .viewImg(viewImg)
                .thumbnailImg(thumbnailImg)
                .price(price)
                .build();

        Item item = new Item(itemRequestDto);
        itemRepository.save(item);
    }

//member 찾기
    private Member getMember(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));
        return member;
    }
}