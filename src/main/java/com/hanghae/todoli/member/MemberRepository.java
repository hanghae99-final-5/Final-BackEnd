package com.hanghae.todoli.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByUsername(String username);

    Optional<Member> findByNickname(String nickname);

    //아이디 찾기
    @Query("select m.username from Member m where m.nickname = :nickname")
    String findUsername(@Param("nickname") String nickname);


}
