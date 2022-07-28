package com.hanghae.todoli.inventory.repository;

import com.hanghae.todoli.item.Dto.ExistItemListDto;
import com.hanghae.todoli.item.Dto.QExistItemListDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.hanghae.todoli.character.QCharacter.character;
import static com.hanghae.todoli.inventory.QInventory.inventory;
import static com.hanghae.todoli.item.QItem.item;

@RequiredArgsConstructor
public class InventoryRepositoryImpl implements InventoryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ExistItemListDto> findExistItems(Long charId) {

        return queryFactory
                .select(new QExistItemListDto(
                        item.id,
                        item.name,
                        item.viewImg,
                        item.category)
                )
                .from(inventory)
                .join(inventory.character, character)
                .join(inventory.item,item)
                .where(
                        inventory.character.id.eq(charId)
                )
                .fetch();
    }
}
