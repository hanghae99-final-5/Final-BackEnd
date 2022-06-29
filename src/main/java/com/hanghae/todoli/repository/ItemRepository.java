package com.hanghae.todoli.repository;

import com.hanghae.todoli.models.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
