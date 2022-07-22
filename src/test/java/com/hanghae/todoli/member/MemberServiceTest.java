package com.hanghae.todoli.member;

import com.hanghae.todoli.character.Character;
import com.hanghae.todoli.equipitem.EquipItem;
import com.hanghae.todoli.exception.CustomException;
import com.hanghae.todoli.member.dto.LoginRequestDto;
import com.hanghae.todoli.member.dto.SignupRequestDto;
import com.hanghae.todoli.security.jwt.JwtTokenProvider;
import org.apache.catalina.connector.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Nested;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static org.mockito.BDDMockito.given;

@Transactional
@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private BasicItemRegister basicItemRegister;
    @Mock
    private JwtTokenProvider jwtTokenProvider;

    MemberService memberService;

    /*
    중복 확인을 위한 유저 생성
    유저 생성하면서 EquipItem Character 같이 생성 돼서 넣어놓음.
     */
    EquipItem equipItem = new EquipItem(
            1L,
            null,
            null,
            null
    );
    Character character = new Character(
            1L,
            "https://twodo-li.s3.ap-northeast-2.amazonaws.com/spon.png",
            100,
            0,
            100,
            0,
            1,
            10000,
            null,
            equipItem
    );
    Member existMember = new Member(
            "test@naver.com",
            "test",
            "password",
            false,
            character
    );
    String username;
    String nickname;
    String password;

    @BeforeEach
    void beforeEach() {
        this.memberService = new MemberService(
                passwordEncoder,
                memberRepository,
                basicItemRegister,
                jwtTokenProvider
        );
        this.passwordEncoder = new BCryptPasswordEncoder();

        username = "test1@naver.com";
        nickname = "testNick";
        password = "testPw";

        existMember.setId(1L);

        //비밀번호 암호화
        existMember.setPassword(passwordEncoder.encode(existMember.getPassword()));
    }

    @Test
    @WithMockUser
    @DisplayName("회원가입 성공")
    void signup() {
        //given
        SignupRequestDto signupRequestDto = new SignupRequestDto();
        signupRequestDto.setUsername(username);
        signupRequestDto.setNickname(nickname);
        signupRequestDto.setPassword(password);


        //when
        String result = memberService.signup(signupRequestDto);
        //then
        Assertions.assertEquals("회원가입 완료", result);
    }

    @Test
    @DisplayName("회원가입 실패 - username 중복")
    void SignupFail1() {
        //given
        username = "test@naver.com";

        SignupRequestDto signupRequestDto = new SignupRequestDto();
        signupRequestDto.setUsername(username);
        signupRequestDto.setNickname(nickname);
        signupRequestDto.setPassword(password);

        given(memberRepository.findByUsername(signupRequestDto.getUsername()))
                .willReturn(Optional.ofNullable(existMember));
        //when

        CustomException exception = Assertions.assertThrows(CustomException.class,
                () -> memberService.signup(signupRequestDto));

        //then
        Assertions.assertEquals("이미 중복된 아이디입니다.", exception.getErrorCode().getMessage());
    }

    @Test
    @DisplayName("로그인 성공")
    void login() {
        //given
        this.memberService = new MemberService(
                passwordEncoder,
                memberRepository,
                basicItemRegister,
                jwtTokenProvider
        );
        username = "test@naver.com";
        password = "password";

        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setUsername(username);
        loginRequestDto.setPassword(password);

        given(memberRepository.findByUsername(loginRequestDto.getUsername()))
                .willReturn(Optional.ofNullable(existMember));

        HttpServletResponse response = new Response();
        //when
        Member result = memberService.login(loginRequestDto, response);

        //then
        Assertions.assertEquals(existMember, result);
        Assertions.assertEquals(existMember.getId(), result.getId());
        Assertions.assertEquals(existMember.getUsername(), result.getUsername());
        Assertions.assertEquals(existMember.getNickname(), result.getNickname());
        Assertions.assertEquals(existMember.getPassword(), result.getPassword());
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 오류")
    void loginFail1() {
        //given
        this.memberService = new MemberService(
                passwordEncoder,
                memberRepository,
                basicItemRegister,
                jwtTokenProvider
        );
        username = "test@naver.com";
        password = "wrongPassword";

        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setUsername(username);
        loginRequestDto.setPassword(password);

        given(memberRepository.findByUsername(loginRequestDto.getUsername()))
                .willReturn(Optional.ofNullable(existMember));

        HttpServletResponse response = new Response();
        //when
        CustomException exception = Assertions.assertThrows(CustomException.class,
                () -> memberService.login(loginRequestDto, response));

        //then
        Assertions.assertEquals("비밀번호가 일치하지 않습니다.",exception.getErrorCode().getMessage());
    }
    @Test
    @DisplayName("로그인 실패 - 아이디가 존재하지 않음")
    void loginFail2() {
        //given
        this.memberService = new MemberService(
                passwordEncoder,
                memberRepository,
                basicItemRegister,
                jwtTokenProvider
        );
        username = "test123@naver.com";
        password = "password";

        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setUsername(username);
        loginRequestDto.setPassword(password);

        HttpServletResponse response = new Response();
        //when
        CustomException exception = Assertions.assertThrows(CustomException.class,
                () -> memberService.login(loginRequestDto, response));

        //then
        Assertions.assertEquals("해당 유저 정보를 찾을 수 없습니다.",exception.getErrorCode().getMessage());

    }
}