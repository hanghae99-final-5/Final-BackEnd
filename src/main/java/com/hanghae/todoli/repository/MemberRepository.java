package com.hanghae.todoli.repository;

import com.hanghae.todoli.models.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {


    Optional<Member> findByUsername(String username);
}
