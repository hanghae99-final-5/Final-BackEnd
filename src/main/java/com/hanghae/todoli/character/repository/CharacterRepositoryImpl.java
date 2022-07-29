package com.hanghae.todoli.character.repository;

import com.hanghae.todoli.character.Dto.QThumbnailDto;
import com.hanghae.todoli.character.Dto.ThumbnailDto;
import com.hanghae.todoli.equipitem.EquipItemDto;
import com.hanghae.todoli.equipitem.QEquipItemDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.hanghae.todoli.character.QCharacter.character;
import static com.hanghae.todoli.equipitem.QEquipItem.equipItem;
import static com.hanghae.todoli.inventory.QInventory.inventory;
import static com.hanghae.todoli.item.QItem.item;

@RequiredArgsConstructor
public class CharacterRepositoryImpl implements CharacterRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<EquipItemDto> getEquipItems(Long characterId) {


        return queryFactory
                .select(new QEquipItemDto(
                        item.id,
                        item.equipImg,
                        item.category
                ))
                .from(inventory)
                .join(inventory.item, item)
                .join(inventory.character, character)
                .join(character.equipItem, equipItem)
                .where(
                        character.id.eq(characterId),
                        item.id.in(
                                equipItem.hairId,
                                equipItem.accessoryId,
                                equipItem.clothId)
                ).fetch();

    }

    @Override
    public List<ThumbnailDto> getThumbnailEquipItems(Long characterId) {

        return queryFactory
                .select(new QThumbnailDto(
                        item.id,
                        item.thumbnailImg,
                        item.category
                ))
                .from(inventory)
                .join(inventory.item, item)
                .join(inventory.character, character)
                .join(character.equipItem, equipItem)
                .where(
                        character.id.eq(characterId),
                        item.id.in(
                                equipItem.hairId,
                                equipItem.accessoryId,
                                equipItem.clothId)
                ).fetch();

    }
}
