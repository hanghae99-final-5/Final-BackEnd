package com.hanghae.todoli.controller;

import com.hanghae.todoli.dto.LoginRequestDto;
import com.hanghae.todoli.dto.SignupRequestDto;
import com.hanghae.todoli.models.Member;
import com.hanghae.todoli.security.jwt.JwtTokenProvider;
import com.hanghae.todoli.security.jwt.UserDetailsImpl;
import com.hanghae.todoli.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    private final JwtTokenProvider jwtTokenProvider;

    //회원가입
    @PostMapping("/api/users/signup")
    public void signup(@RequestBody SignupRequestDto signupRequestDto) {
        memberService.signup(signupRequestDto);
        System.out.println("회원가입 완료");
    }

    //로그인
    @PostMapping("/api/users/login")
    public void login(HttpServletResponse response, @RequestBody LoginRequestDto loginRequestDto) {
        Member member = memberService.login(loginRequestDto);
        String token = jwtTokenProvider.createToken(member.getUsername());
        response.addHeader("Authorization",token);
        System.out.println(token);
    }
}
