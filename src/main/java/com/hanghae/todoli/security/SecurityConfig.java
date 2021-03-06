package com.hanghae.todoli.security;

import com.hanghae.todoli.security.jwt.JwtAuthenticationFilter;
import com.hanghae.todoli.security.jwt.JwtEntryPoint;
import com.hanghae.todoli.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtEntryPoint jwtEntryPoint;

    @Bean   // 비밀번호 암호화
    public BCryptPasswordEncoder encodePassword() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public HttpFirewall defaultHttpFirewall() {
        return new DefaultHttpFirewall();
    }

    // 정적 자원에 대해서는 Security 설정을 적용하지 않음.
    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/h2-console/**");
//        web.ignoring().mvcMatchers("/favicon-32x32.png");
//        web.ignoring().mvcMatchers("/favicon-96x96.png");
//        web.ignoring().mvcMatchers("/favicon-16x16.png");
//        web.ignoring().mvcMatchers("/**/favicon-32x32.png");
//        web.ignoring().mvcMatchers("/**/favicon-96x96.png");
//        web.ignoring().mvcMatchers("/**/favicon-16x16.png");
        web.httpFirewall(defaultHttpFirewall());
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.headers().frameOptions().disable()
                .and()
                .cors();
        http.authorizeRequests()
                // api 요청 접근허용
                .antMatchers("/api/items").permitAll()
                .antMatchers("/api/users/**").permitAll()
                .antMatchers("/api/login/**").permitAll()
                .antMatchers("/h2-console/**").permitAll()
                .antMatchers("/health").permitAll()
                // 그 외 모든 요청은 인증과정 필요
                .anyRequest().authenticated()
                 .and()
                // 토큰을 활용하면 세션이 필요 없으므로 STATELESS로 설정하여 Session을 사용하지 않는다.
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling().authenticationEntryPoint(jwtEntryPoint)
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
    }
}
