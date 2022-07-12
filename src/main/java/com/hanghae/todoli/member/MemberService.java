package com.hanghae.todoli.member;

import com.hanghae.todoli.exception.CustomException;
import com.hanghae.todoli.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final BasicItemRegister basicItemRegister;

    //회원가입
    @Transactional
    public void signup(SignupRequestDto signupRequestDto) {
        String username = signupRequestDto.getUsername();
        String nickname = signupRequestDto.getNickname();

        idCheck(username);

        String password = passwordEncoder.encode(signupRequestDto.getPassword());

        // TODO : 2022/07/12 refactoring - 종석
        //멤버생성과 동시에 캐릭터, 장착아이템 같이 생성, 기본아이템 제공 및 장착까지
        basicItemRegister.basicItem(username, nickname, password);
    }

    @Transactional
    public Member login(LoginRequestDto loginRequestDto) {
        String username = loginRequestDto.getUsername();
        Member Member = memberRepository.findByUsername(username).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_MEMBER)
        );

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), Member.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_NOT_SAME);
        }
        return Member;
    }

    //아이디 중복확인
    private void idCheck(String username) {
        Optional<Member> found = memberRepository.findByUsername(username);
        if (found.isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATED_ID);
        }
    }
}
