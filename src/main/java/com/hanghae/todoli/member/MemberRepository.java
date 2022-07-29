package com.hanghae.todoli.member;

<<<<<<< HEAD
import com.hanghae.todoli.character.Character;
=======
>>>>>>> 2389428 (사용자 추천 수정)
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

<<<<<<< HEAD

=======
>>>>>>> 2389428 (사용자 추천 수정)
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByUsername(String username);
<<<<<<< HEAD
    Optional<Member> findByNickname(String nickname);
    //아이디 찾기
    @Query("select m.username from Member m where m.nickname = :nickname")
    String findUsername(@Param("nickname") String nickname);

    //비밀번호 찾기
    Optional<Member> findByPassword(String curPassword);

    @Query("select m from Member m")
    List<Member> findAllByLevelRanking(Pageable pageable);

=======


    // 사용자 추천
    @Query("select m " +
            "from Member m " +
            "join m.character c " +
            "where m.matchingState = false " +
            "and " +
            "m.id not in (:memberId) " +
            "and " +
            "c.level between :level - 3 and :level + 3")
    List<Member>findUserByLevel(Pageable pageable, @Param("level") int level, @Param("memberId") Long memberId);
>>>>>>> 2389428 (사용자 추천 수정)
}
