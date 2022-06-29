package com.hanghae.todoli.repository;

import com.hanghae.todoli.models.Character;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CharacterRepository extends JpaRepository<Character, Long> {
}
