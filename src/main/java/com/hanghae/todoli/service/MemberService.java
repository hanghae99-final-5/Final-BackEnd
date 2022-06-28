package com.hanghae.todoli.service;

import com.hanghae.todoli.dto.LoginRequestDto;
import com.hanghae.todoli.dto.SignupRequestDto;
import com.hanghae.todoli.security.jwt.JwtTokenProvider;
import com.hanghae.todoli.models.Member;
import com.hanghae.todoli.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    //회원가입
    public void signup(SignupRequestDto signupRequestDto) {
        String username = signupRequestDto.getUsername();
        String nickname = signupRequestDto.getNickname();

        idCheck(username);

        String password = passwordEncoder.encode(signupRequestDto.getPassword());

        Member member = new Member(username, nickname, password);
        memberRepository.save(member);
    }

    //아이디 중복확인
    public Member login(LoginRequestDto loginRequestDto) {
        String username = loginRequestDto.getUsername();
        Member member = memberRepository.findByUsername(username).orElseThrow(
                () -> new IllegalArgumentException("아이디가 존재하지 않습니다.")
        );

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        return member;
    }

    private void idCheck(String username) {
        Optional<Member> found = memberRepository.findByUsername(username);
        if (found.isPresent()) {
            throw new IllegalArgumentException("이미 중복된 아이디입니다.");
        }
    }
}
