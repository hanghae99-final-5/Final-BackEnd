package com.hanghae.todoli.matching;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MatchingRepository extends JpaRepository<Matching, Long> {

    //내 아이디로 매칭 상대 아이디 찾기
    @Query("select m from Matching m where m.requesterId = :memberId or m.respondentId = :memberId")
    Optional<Matching> getMatching(@Param("memberId")Long memberId);

}
