package com.hanghae.todoli.inventory;

import com.hanghae.todoli.character.Character;
import com.hanghae.todoli.item.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByCharacterAndItem(Character character, Item item);
}
