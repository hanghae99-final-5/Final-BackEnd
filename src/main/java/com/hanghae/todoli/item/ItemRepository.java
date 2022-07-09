package com.hanghae.todoli.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("select i from Item i where i.id not in (1L,2L,3L)")
    List<Item> findAllExceptBasic();
}
