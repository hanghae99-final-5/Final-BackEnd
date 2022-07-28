package com.hanghae.todoli.character.repository;

import com.hanghae.todoli.character.Character;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CharacterRepository extends JpaRepository<Character, Long>, CharacterRepositoryCustom {
}
