package com.hanghae.todoli.member;

import com.hanghae.todoli.exception.CustomException;
import com.hanghae.todoli.exception.ErrorCode;
import com.hanghae.todoli.security.jwt.JwtTokenProvider;
import com.hanghae.todoli.member.dto.LoginRequestDto;
import com.hanghae.todoli.member.dto.SignupRequestDto;
import com.hanghae.todoli.utils.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import javax.servlet.http.HttpServletResponse;

import javax.activation.FileDataSource;
import javax.mail.MessagingException;

import javax.mail.internet.MimeMessage;

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

    //아이디 찾기
    @Transactional
    public String findUsername(String nickname) {
        Member member = memberRepository.findByNickname(nickname).orElse(null);
        if(member == null)
            throw new IllegalArgumentException("회원가입한 이력이 없습니다.");

        return memberRepository.findUsername(nickname);
    }

    //비밀번호 찾기

    private final JavaMailSender javaMailSender;
    @Transactional
    public void findPassword(String username) throws MessagingException {
        Member member = memberRepository.findByUsername(username).orElseThrow(
                () -> new IllegalArgumentException("해당 이메일이 존재하지 않습니다.")
        );

        String pw = "";
        for (int i = 0; i < 12; i++) {
            pw += (char) ((Math.random() * 26) + 97);
        }

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        mimeMessageHelper.setFrom("wjstjddud119@naver.com");
        mimeMessageHelper.setTo(member.getUsername());
        mimeMessageHelper.setSubject("[TwoDoRi]임시 비밀번호 발급 안내");

        StringBuilder body = new StringBuilder();
        body.append("안녕하세요" + member.getNickname() + "님!\n\n\n");
        body.append(member.getNickname() + "님의 임시 비밀번호는" + pw + "입니다.");
        String content = "메일 테스트 내용" + "<img src=\"https://drive.google.com/uc?id=1SF2WHxR-U52qCfDb45G3Ptj0z8A2vhMz\">";
        //mimeMessageHelper.addInline("zz", new FileDataSource("C://Temp/cute_cat.jpg"));
        mimeMessageHelper.setText(content);
        mimeMessageHelper.setText(body.toString());


        javaMailSender.send(mimeMessage);

        String password = passwordEncoder.encode(pw);
        member.pwUpdate(password);
    }
}
