package com.hanghae.todoli.item;

import com.hanghae.todoli.character.Character;
import com.hanghae.todoli.equipitem.EquipItem;
import com.hanghae.todoli.equipitem.EquipItemDto;
import com.hanghae.todoli.exception.CustomException;
import com.hanghae.todoli.exception.ErrorCode;
import com.hanghae.todoli.inventory.Inventory;
import com.hanghae.todoli.inventory.repository.InventoryRepository;
import com.hanghae.todoli.item.Dto.ExistItemListDto;
import com.hanghae.todoli.item.Dto.ItemRequestDto;
import com.hanghae.todoli.item.Dto.ItemResponseDto;
import com.hanghae.todoli.member.Member;
import com.hanghae.todoli.member.MemberRepository;
import com.hanghae.todoli.security.UserDetailsImpl;
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
    private final MemberRepository memberRepository;

    //가지고있는 아이템들 조회
    @Transactional
    public List<ExistItemListDto> getExistItemList(UserDetailsImpl userDetails) {
        Long memberId = userDetails.getMember().getId();
        return inventoryRepository.findExistItems(memberId);
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
        Inventory exist = inventoryRepository.findByCharacterAndItem(character, buyItem).orElse(null);

        if (exist == null) {
            //계산
            if (character.getMoney() >= buyItem.getPrice()) {
                character.minMoney(buyItem.getPrice());

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

        return EquipItemDto.builder()
                .itemId(item.getId())
                .equipImg(item.getEquipImg())
                .category(item.getCategory())
                .build();
    }

    //아이템 등록
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
        return memberRepository.findById(memberId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));
    }

    //아이템 찾기
    private Item findItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(
                () -> new CustomException(ErrorCode.NO_ITEM));
    }
}