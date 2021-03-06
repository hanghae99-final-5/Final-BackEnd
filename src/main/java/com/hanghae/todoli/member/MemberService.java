package com.hanghae.todoli.member;

import com.hanghae.todoli.character.CharacterImg;
import com.hanghae.todoli.character.Dto.ThumbnailDto;
import com.hanghae.todoli.character.repository.CharacterRepository;
import com.hanghae.todoli.exception.CustomException;
import com.hanghae.todoli.exception.ErrorCode;
import com.hanghae.todoli.member.dto.LoginRequestDto;
import com.hanghae.todoli.member.dto.PasswordUpdateDto;
import com.hanghae.todoli.member.dto.RankingDto;
import com.hanghae.todoli.member.dto.SignupRequestDto;
import com.hanghae.todoli.security.UserDetailsImpl;
import com.hanghae.todoli.security.jwt.JwtTokenProvider;
import com.hanghae.todoli.utils.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final BasicItemRegister basicItemRegister;
    private final JwtTokenProvider jwtTokenProvider;
    private final CharacterRepository characterRepository;
    private final JavaMailSender javaMailSender;
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
            throw new CustomException(ErrorCode.NOT_FOUND_MEMBER);

        return memberRepository.findUsername(nickname);
    }

    //비밀번호 찾기

    @Transactional
    public void findPassword(String username) throws MessagingException {
        Member member = memberRepository.findByUsername(username).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_MEMBER)
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

        mimeMessageHelper.setText(content, true);


        javaMailSender.send(mimeMessage);

        String password = passwordEncoder.encode(pw);
        member.pwUpdate(password);
    }

    //비밀번호 변경
    @Transactional
    public void updatePassword(PasswordUpdateDto updateDto, UserDetailsImpl userDetails) {
        Member member = userDetails.getMember();

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String curPassword = updateDto.getCurPassword();
        if(!bCryptPasswordEncoder.matches(curPassword, userDetails.getPassword()))
            throw new CustomException(ErrorCode.PASSWORD_NOT_SAME);

        String changePassword = updateDto.getChangePassword();
        if(bCryptPasswordEncoder.matches(changePassword, userDetails.getPassword()))
            throw new CustomException(ErrorCode.PASSWORD_NOT_SAME);

        String ecPassword = passwordEncoder.encode(changePassword);
        member.pwUpdate(ecPassword);
        memberRepository.save(member);
    }

    //랭킹 업데이트
    @Transactional
    public List<RankingDto> updateRanking() {
        Pageable pageable = PageRequest.of(0, 5, Sort.Direction.DESC, "level");
        List<Member> characterRankList = memberRepository.findAllByLevelRanking(pageable);

        List<RankingDto>rankingDtoList = new ArrayList<>();
        for(Member member : characterRankList){
            Long memberId = member.getId();
            String nickname = member.getNickname();
            String thumbnailCharImg = new CharacterImg().getThumbnailCharImg();
            List<ThumbnailDto> thumbnailEquipItems = characterRepository.getThumbnailEquipItems(memberId);
            RankingDto ranking = RankingDto.builder()
                    .memberId(memberId)
                    .nickname(nickname)
                    .thumbnailCharImg(thumbnailCharImg)
                    .equipItems(thumbnailEquipItems)
                    .build();
            rankingDtoList.add(ranking);
        }
        return rankingDtoList;
    }
}
