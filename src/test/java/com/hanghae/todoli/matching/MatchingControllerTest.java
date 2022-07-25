package com.hanghae.todoli.matching;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae.todoli.member.Member;
import com.hanghae.todoli.security.MockSpringSecurityFilter;
import com.hanghae.todoli.security.SecurityConfig;
import com.hanghae.todoli.security.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MatchingController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = SecurityConfig.class
                )})
@MockBean(JpaMetamodelMappingContext.class)
class MatchingControllerTest {

    @MockBean
    MatchingService matchingService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;
    private Principal mockPrincipal;

    private void mockUserSetup() {
        //mock 테스트 유저 생성
        String username = "테스트 유저";
        String nickname = "테스트";
        String password = "test123";
        Member testMember = new Member(username,nickname,password,false,null);
        UserDetailsImpl testUserDetail = new UserDetailsImpl(testMember);
        mockPrincipal = new UsernamePasswordAuthenticationToken(testUserDetail,"",null);
    }

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity(new MockSpringSecurityFilter()))
                .build();
    }
    @Test
    @DisplayName("사용자 검색")
    void searchMember() throws Exception{
        this.mockUserSetup();

        mvc.perform(get("/api/users/{username}",
                        "test@naver.com")
                        .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andDo(print());

        verify(matchingService,times(1))
                .searchMember(anyString(),any(UserDetailsImpl.class));
    }

    @Test
    @DisplayName("매칭 초대")
    void inviteMatching() throws Exception{
        this.mockUserSetup();

        mvc.perform(post("/api/users/invitation/{memberId}",
                        1)
                        .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andDo(print());

        verify(matchingService,times(1))
                .inviteMatching(anyLong(),any(UserDetailsImpl.class));
    }

    @Test
    @DisplayName("매칭 취소")
    void cancelMatching() throws Exception{
        this.mockUserSetup();

        mvc.perform(patch("/api/users/cancel/{memberId}",
                        1)
                        .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andDo(print());

        verify(matchingService,times(1))
                .cancelMatching(anyLong(),any(UserDetailsImpl.class));
    }

    @Test
    @DisplayName("매칭 수락")
    void acceptMatching() throws Exception{
        this.mockUserSetup();

        mvc.perform(post("/api/users/acceptance/{senderId}",
                        1)
                        .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andDo(print());

        verify(matchingService,times(1))
                .acceptMatching(anyLong(),any(UserDetailsImpl.class));
    }

    @Test
    @DisplayName("자신 매칭상태 체크")
    void myMatchingState() throws Exception{
        this.mockUserSetup();
        mvc.perform(get("/api/users/check")
                        .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andDo(print());
    }
}