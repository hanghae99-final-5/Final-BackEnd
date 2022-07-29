package com.hanghae.todoli.inventory.repository;

import com.hanghae.todoli.item.Dto.ExistItemListDto;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InventoryRepositoryCustom {

    List<ExistItemListDto> findExistItems(@Param("charId") Long charId);
}
