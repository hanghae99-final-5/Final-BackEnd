package com.hanghae.todoli.member;

import com.hanghae.todoli.exception.CustomException;
import com.hanghae.todoli.exception.ErrorCode;
import com.hanghae.todoli.security.jwt.JwtTokenProvider;
import com.hanghae.todoli.member.dto.LoginRequestDto;
import com.hanghae.todoli.member.dto.SignupRequestDto;
import com.hanghae.todoli.utils.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final BasicItemRegister basicItemRegister;
    private final JwtTokenProvider jwtTokenProvider;
    private final Validator validator;

    //회원가입
    @Transactional
    public String signup(SignupRequestDto signupRequestDto) {

        validator.validSignup(signupRequestDto);

        String username = signupRequestDto.getUsername();
        String nickname = signupRequestDto.getNickname();
        String password = passwordEncoder.encode(signupRequestDto.getPassword());

        //멤버생성과 동시에 캐릭터, 장착아이템 같이 생성, 기본아이템 제공 및 장착까지
        Member member = basicItemRegister.basicItem(username, nickname, password);
        memberRepository.save(member);

        return "회원가입 완료";
    }

    @Transactional
    public Member login(LoginRequestDto loginRequestDto, HttpServletResponse response) {
        String username = loginRequestDto.getUsername();
        Member member = memberRepository.findByUsername(username).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_MEMBER)
        );

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), member.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_NOT_SAME);
        }

        //토큰 생성
        String token = jwtTokenProvider.createToken(member.getUsername(), member.getNickname());
        response.addHeader("Authorization", token);
        System.out.println(token);

        return member;
    }
}
