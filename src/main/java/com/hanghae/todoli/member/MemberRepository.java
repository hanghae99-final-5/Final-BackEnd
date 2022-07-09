package com.hanghae.todoli.member;

import com.hanghae.todoli.character.Character;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByUsername(String username);

    //아이디 찾기
    @Query("select m.username from Member m where m.nickname = :nickname")
    String findUsername(@Param("nickname") String nickname);

    //비밀번호 찾기
    Optional<Member> findByPassword(String curPassword);

    @Query("select m from Member m")
    List<Member> findAllByLevelRanking(Pageable pageable);

}
