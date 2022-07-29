package com.hanghae.todoli.character.repository;

import com.hanghae.todoli.character.Dto.ThumbnailDto;
import com.hanghae.todoli.equipitem.EquipItemDto;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CharacterRepositoryCustom {
    List<EquipItemDto> getEquipItems(@Param("characterId") Long characterId);

    List<ThumbnailDto> getThumbnailEquipItems(@Param("characterId") Long characterId);
}
