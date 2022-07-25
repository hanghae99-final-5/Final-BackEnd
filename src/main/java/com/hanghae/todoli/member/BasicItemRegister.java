package com.hanghae.todoli.member;

import com.hanghae.todoli.character.Character;
import com.hanghae.todoli.character.CharacterRepository;
import com.hanghae.todoli.equipitem.EquipItem;
import com.hanghae.todoli.equipitem.EquipItemRepository;
import com.hanghae.todoli.exception.CustomException;
import com.hanghae.todoli.exception.ErrorCode;
import com.hanghae.todoli.inventory.Inventory;
import com.hanghae.todoli.inventory.InventoryRepository;
import com.hanghae.todoli.item.Item;
import com.hanghae.todoli.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BasicItemRegister {
    private final MemberRepository memberRepository;
    private final CharacterRepository characterRepository;
    private final EquipItemRepository equipItemRepository;
    private final ItemRepository itemRepository;
    private final InventoryRepository inventoryRepository;

    public Member basicItem(String username, String nickname, String password) {
        Item basicAccessory = itemRepository.findById(1L).orElseThrow(
                () -> new CustomException(ErrorCode.NO_ITEM)
        );
        Item basicHair = itemRepository.findById(2L).orElseThrow(
                () -> new CustomException(ErrorCode.NO_ITEM)
        );
        Item basicCloth = itemRepository.findById(3L).orElseThrow(
                () -> new CustomException(ErrorCode.NO_ITEM)
        );
        EquipItem equipItem = new EquipItem(1L, 2L, 3L);
        equipItemRepository.save(equipItem);
        Character character = new Character(equipItem);
        characterRepository.save(character);
        Inventory addAccessory = new Inventory(basicAccessory, character);
        inventoryRepository.save(addAccessory);
        Inventory addHair = new Inventory(basicHair, character);
        inventoryRepository.save(addHair);
        Inventory addCloth = new Inventory(basicCloth, character);
        inventoryRepository.save(addCloth);

        return new Member(username, nickname, password, false, character);
    }
}
