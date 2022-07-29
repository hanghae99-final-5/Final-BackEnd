package com.hanghae.todoli.inventory.repository;

import com.hanghae.todoli.character.Character;
import com.hanghae.todoli.inventory.Inventory;
import com.hanghae.todoli.item.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long>, InventoryRepositoryCustom {
    Optional<Inventory> findByCharacterAndItem(Character character, Item item);
}
