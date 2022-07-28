package com.hanghae.todoli.character.Dto;

import com.hanghae.todoli.character.repository.CharacterRepository;
import com.hanghae.todoli.equipitem.EquipItem;
import com.hanghae.todoli.equipitem.EquipItemDto;
import com.hanghae.todoli.item.Item;
import com.hanghae.todoli.item.ItemRepository;
import com.hanghae.todoli.member.Member;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Component
@Data
public class ThumbnailDtoList {

    private final ItemRepository itemRepository;
    private final CharacterRepository characterRepository;

    public List<ThumbnailDto> getThumbnailDtos(Member info) {

        Long charId = info.getCharacter().getId();
        List<ThumbnailDto> thumbnailEquipItems = characterRepository.getThumbnailEquipItems(charId);
        Collections.sort(thumbnailEquipItems, new Comparator<ThumbnailDto>() {
            @Override
            public int compare(ThumbnailDto o1, ThumbnailDto o2) {
                char c1 = o1.getCategory().toString().charAt(2);
                char c2 = o2.getCategory().toString().charAt(2);
                if (c1 < c2) {
                    return 1;
                }
                return -1;
            }
        });
        return thumbnailEquipItems;
    }
}
