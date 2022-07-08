package com.hanghae.todoli.member;


import com.hanghae.todoli.exception.CustomException;
import com.hanghae.todoli.exception.ErrorCode;
import com.hanghae.todoli.security.jwt.JwtTokenProvider;
import com.hanghae.todoli.member.dto.LoginRequestDto;
import com.hanghae.todoli.member.dto.SignupRequestDto;
import com.hanghae.todoli.utils.Validator;
import com.hanghae.todoli.character.Character;
import com.hanghae.todoli.character.CharacterRepository;
import com.hanghae.todoli.equipitem.EquipItem;
import com.hanghae.todoli.equipitem.EquipItemRepository;
import com.hanghae.todoli.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

        //한 번에 여러 내용
//        StringBuilder body = new StringBuilder();
//        body.append("안녕하세요" + member.getNickname() + "님!\n\n\n");
//        body.append(member.getNickname() + "님의 임시 비밀번호는" + pw + "입니다.");

        String content ="";
        content+= "<img src=\"https://drive.google.com/uc?id=1SGWzVrlaSnIm_V95GgBvjdI56FvLn5hH\">";
        content+= "<br>";
        content+= "<br>";
        content+= "<div style='margin:100px;'>";
        content+= "<h1> 안녕하세요 " + member.getNickname() +"님!! TwoDoRi입니다. </h1>";
        content+= "<br>";
        content+= "<br>";
        content+= "<div align='center' style='border:1px solid black; font-family:verdana';>";
        content+= "<h3 style='color:blue;'>임시 비밀번호 입니다.</h3>";
        content+= "<div style='font-size:130%'>";
        content+= "password : <strong>";
        content+= pw+"</strong><div><br/> ";
        content+= "</div>";

        //mimeMessageHelper.addInline("zz", new FileDataSource("C://Temp/cute_cat.jpg")); -> 파일 첨부
        mimeMessageHelper.setText(content, true);
        //mimeMessageHelper.setText(body.toString());


        javaMailSender.send(mimeMessage);

        String password = passwordEncoder.encode(pw);
        member.pwUpdate(password);
    }

    //비밀번호 변경
    //변경 비밀번호 확인은 프론트단에서

    @Transactional
    public void updatePassword(PasswordUpdateDto updateDto, UserDetailsImpl userDetails) {
        Member member = userDetails.getMember();

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String curPassword = updateDto.getCurPassword();
        if(!bCryptPasswordEncoder.matches(curPassword, userDetails.getPassword()))
            throw new IllegalArgumentException("현재 비밀번호를 잘못 입력하셨습니다.");

        String changePassword = updateDto.getChangePassword();
        if(bCryptPasswordEncoder.matches(changePassword, userDetails.getPassword()))
            throw new IllegalArgumentException("변경 비밀번호와 현재 비밀번호와 같습니다.");

        String ecPassword = passwordEncoder.encode(changePassword);
        member.pwUpdate(ecPassword);
        memberRepository.save(member);
    }
}
