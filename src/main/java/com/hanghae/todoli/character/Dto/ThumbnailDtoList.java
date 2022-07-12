package com.hanghae.todoli.character.Dto;

import com.hanghae.todoli.equipitem.EquipItem;
import com.hanghae.todoli.item.Item;
import com.hanghae.todoli.item.ItemRepository;
import com.hanghae.todoli.member.Member;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Data
public class ThumbnailDtoList {

    private final ItemRepository itemRepository;

    public List<ThumbnailDto> getThumbnailDtos(Member Info) {
        List<ThumbnailDto> myEquipItemList = new ArrayList<>();
        EquipItem myEquipItem = Info.getCharacter().getEquipItem();
        Long hairId = myEquipItem.getHairId();
        Long clothId = myEquipItem.getClothId();
        Long accessoryId = myEquipItem.getAccessoryId();
        if (hairId != null) {
            Item hair = itemRepository.findById(hairId).orElse(null);
            ThumbnailDto thumbnailDto1 = new ThumbnailDto(hair.getId(), hair.getThumbnailImg(), hair.getCategory());
            myEquipItemList.add(thumbnailDto1);
        }
        if (clothId != null) {
            Item cloth = itemRepository.findById(clothId).orElse(null);
            ThumbnailDto thumbnailDto2 = new ThumbnailDto(cloth.getId(), cloth.getThumbnailImg(), cloth.getCategory());
            myEquipItemList.add(thumbnailDto2);
        }
        if (accessoryId != null) {
            Item accessory = itemRepository.findById(accessoryId).orElse(null);
            ThumbnailDto thumbnailDto3 = new ThumbnailDto(accessory.getId(), accessory.getThumbnailImg(), accessory.getCategory());
            myEquipItemList.add(thumbnailDto3);
        }
        return myEquipItemList;
    }
}
