package com.hanghae.todoli.member;

import com.hanghae.todoli.googleLogin.GetSocialOAuthRes;
import com.hanghae.todoli.googleLogin.OAuthService;
import com.hanghae.todoli.googleLogin.SocialLoginType;
import com.hanghae.todoli.member.dto.LoginRequestDto;
import com.hanghae.todoli.member.dto.SignupRequestDto;
import com.hanghae.todoli.security.UserDetailsImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    private final OAuthService oAuthService;

    //회원가입
    @ApiResponses({
            @ApiResponse(code=201, message="회원가입 성공"),
            @ApiResponse(code=400, message="실패")
    })
    @ApiOperation(value = "회원가입 메소드", notes = "회원가입 api 입니다.")
    @PostMapping("/api/users/signup")
    public void signup(@RequestBody SignupRequestDto signupRequestDto) {
        memberService.signup(signupRequestDto);
        System.out.println("회원가입 완료");
    }

    //로그인
    @ApiResponses({
            @ApiResponse(code=200, message="로그인 성공"),
            @ApiResponse(code=400, message="실패")
    })
    @ApiOperation(value = "로그인 메소드", notes = "성공시 jwt 토큰을 헤더에 넣어서 반환합니다.")
    @PostMapping("/api/users/login")
    public void login(HttpServletResponse response, @RequestBody LoginRequestDto loginRequestDto) {
        memberService.login(loginRequestDto,response);
    }

    //구글 로그인
    @ApiIgnore  //swagger에서 api 숨기기
    @GetMapping("/api/users/login/{socialLoginType}") //GOOGLE이 들어올 것이다.
    public String socialLoginRedirect(@PathVariable(name = "socialLoginType") String SocialLoginPath,HttpServletResponse response) throws IOException {
        SocialLoginType socialLoginType = SocialLoginType.valueOf(SocialLoginPath.toUpperCase());

        return oAuthService.request(socialLoginType);
    }

    /**
     * Social Login API Server 요청에 의한 callback 을 처리
     *
     * @param socialLoginPath (GOOGLE, FACEBOOK, NAVER, KAKAO)
     * @param code            API Server 로부터 넘어오는 code
     * @return SNS Login 요청 결과로 받은 Json 형태의 java 객체 (access_token, jwt_token, user_num 등)
     */

    @ApiResponses({
            @ApiResponse(code=201, message="회원가입 성공"),
            @ApiResponse(code=400, message="실패")
    })
    @ApiOperation(value = "OAtuh 메소드", notes = "구글 회원가입 api 입니다.")
    @GetMapping("/api/login/oauth2/code/{socialLoginType}")
    public GetSocialOAuthRes callback(
            @PathVariable(name = "socialLoginType") String socialLoginPath,
            @RequestParam(name = "code") String code, HttpServletResponse response) throws IOException {
        System.out.println(">> 소셜 로그인 API 서버로부터 받은 code :" + code);
        SocialLoginType socialLoginType = SocialLoginType.valueOf(socialLoginPath.toUpperCase());
        return oAuthService.oAuthLogin(socialLoginType, code);
    }

    //아이디 찾기
    @GetMapping("/api/users/find/username/{nickname}")
    public String findUsername(@PathVariable String nickname){
        try{
            String username = memberService.findUsername(nickname);
            return nickname + "님의 아이디는" + username + "입니다.";
        }catch (IllegalArgumentException e){
            return e.getMessage();
        }
    }

    //비밀번호 찾기
    @GetMapping("/api/users/find/password/{username}")
    public String findPassword(@PathVariable String username) throws MessagingException {
        try{
            memberService.findPassword(username);
            return "메일을 전송하였습니다.";
        }catch (IllegalArgumentException e){
            return e.getMessage();
        }
    }

    //비밀번호 변경
    @PatchMapping("/api/users/update/password") //현재 비밀번호, 바꿀 비밀번호, 비밀번호 확인인
    public String updatePassword(@RequestBody PasswordUpdateDto updateDto, @AuthenticationPrincipal UserDetailsImpl userDetails){

            memberService.updatePassword(updateDto, userDetails);
            return "비밀번호 변경이 완료되었습니다.";

    }
}
