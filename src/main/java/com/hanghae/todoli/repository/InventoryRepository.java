package com.hanghae.todoli.repository;

import com.hanghae.todoli.models.Character;
import com.hanghae.todoli.models.Inventory;
import com.hanghae.todoli.models.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByCharacterAndItem(Character character, Item item);
}
