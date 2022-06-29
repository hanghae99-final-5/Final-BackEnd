package com.hanghae.todoli.repository;

import com.hanghae.todoli.models.Matching;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MatchingRepository extends JpaRepository<Matching, Long> {

    //내 아이디로 매칭 상대 아이디 찾기
    @Query("select m from Matching m where m.requesterId = :requesterId")
    Long getRespondentId(@Param("requesterId")Long requesterId);

    @Query("select m from Matching m where m.requesterId = :memberId or m.respondentId = :memberId")
    Matching getMatching(@Param("memberId")Long memberId);
}
