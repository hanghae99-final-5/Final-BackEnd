package com.hanghae.todoli.member;

import com.hanghae.todoli.googleLogin.GetSocialOAuthRes;
import com.hanghae.todoli.googleLogin.OAuthService;
import com.hanghae.todoli.googleLogin.SocialLoginType;
import com.hanghae.todoli.member.dto.LoginRequestDto;
import com.hanghae.todoli.member.dto.SignupRequestDto;
import com.hanghae.todoli.security.jwt.JwtTokenProvider;
import com.nimbusds.oauth2.sdk.ErrorResponse;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    private final OAuthService oAuthService;

    private final JwtTokenProvider jwtTokenProvider;

    //회원가입
    @PostMapping("/api/users/signup")
    public void signup(@RequestBody SignupRequestDto signupRequestDto) {
        memberService.signup(signupRequestDto);
        System.out.println("회원가입 완료");
    }

    //로그인
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공"),
            @ApiResponse(code = 400, message = "1.등록되지 않은 개인 미션 \t\n 2.등록되지 않은 유저 \t\n 3.하루 인증 횟수 초과", response = ErrorResponse.class)
    })
    @ApiOperation(value = "로그인 메소드", notes = "성공시 jwt 토큰을 헤더에 넣어서 반환합니다.")
    @PostMapping("/api/users/login")
    public void login(HttpServletResponse response, @RequestBody LoginRequestDto loginRequestDto) {
        Member member = memberService.login(loginRequestDto);
        String token = jwtTokenProvider.createToken(member.getUsername(), member.getNickname());
        response.addHeader("Authorization", token);
        System.out.println(token);
    }

    //구글 로그인
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

    @GetMapping("/api/login/oauth2/code/{socialLoginType}/callback")
    public GetSocialOAuthRes callback(
            @PathVariable(name = "socialLoginType") String socialLoginPath,
            @RequestParam(name = "code") String code, HttpServletResponse response) throws IOException {
        System.out.println(">> 소셜 로그인 API 서버로부터 받은 code :" + code);
        SocialLoginType socialLoginType = SocialLoginType.valueOf(socialLoginPath.toUpperCase());
        GetSocialOAuthRes getSocialOAuthRes = oAuthService.oAuthLogin(socialLoginType, code);
        String token = getSocialOAuthRes.getAuthorization();
        response.addHeader("Authorization", token);
        return getSocialOAuthRes;
    }
}
