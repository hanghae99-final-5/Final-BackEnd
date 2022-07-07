package com.hanghae.todoli.member;

import com.hanghae.todoli.googleLogin.GetSocialOAuthRes;
import com.hanghae.todoli.googleLogin.OAuthService;
import com.hanghae.todoli.googleLogin.SocialLoginType;
import com.hanghae.todoli.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    @PostMapping("/api/users/login")
    public void login(HttpServletResponse response, @RequestBody LoginRequestDto loginRequestDto) {
        Member member = memberService.login(loginRequestDto);
        String token = jwtTokenProvider.createToken(member.getUsername(),member.getNickname());
        response.addHeader("Authorization", token);
        System.out.println(token);
    }

    //구글 로그인
    @GetMapping("/api/users/login/{socialLoginType}") //GOOGLE이 들어올 것이다.
    public void socialLoginRedirect(@PathVariable(name = "socialLoginType") String SocialLoginPath) throws IOException {
        SocialLoginType socialLoginType = SocialLoginType.valueOf(SocialLoginPath.toUpperCase());
        oAuthService.request(socialLoginType);
    }

    /**
     * Social Login API Server 요청에 의한 callback 을 처리
     *
     * @param socialLoginPath (GOOGLE, FACEBOOK, NAVER, KAKAO)
     * @param code            API Server 로부터 넘어오는 code
     * @return SNS Login 요청 결과로 받은 Json 형태의 java 객체 (access_token, jwt_token, user_num 등)
     */

    @GetMapping("/api/login/oauth2/code/{socialLoginType}")
    public GetSocialOAuthRes callback(
            @PathVariable(name = "socialLoginType") String socialLoginPath,
            @RequestParam(name = "code") String code) throws IOException {
        System.out.println(">> 소셜 로그인 API 서버로부터 받은 code :" + code);
        SocialLoginType socialLoginType = SocialLoginType.valueOf(socialLoginPath.toUpperCase());
        return oAuthService.oAuthLogin(socialLoginType, code);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}
