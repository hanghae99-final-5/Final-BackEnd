package com.hanghae.todoli.alarm;

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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AlarmController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = SecurityConfig.class
                )})
@MockBean(JpaMetamodelMappingContext.class)
class AlarmControllerTest {

    @MockBean
    AlarmService alarmService;

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
        Member testMember = new Member(username, nickname, password, false, null);
        UserDetailsImpl testUserDetail = new UserDetailsImpl(testMember);
        mockPrincipal = new UsernamePasswordAuthenticationToken(testUserDetail, "", null);
    }

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity(new MockSpringSecurityFilter()))
                .build();
    }

    @Test
    @DisplayName("자신의 알람 보여주기")
    void getAlarms() throws Exception {
        this.mockUserSetup();

        mvc.perform(get("/api/alarms")
                        .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andDo(print());

        verify(alarmService,times(1)).getAlarms(any(UserDetailsImpl.class));

    }

    @Test
    @DisplayName("알람 삭제")
    void deleteAlarms() throws Exception{
        this.mockUserSetup();

        mvc.perform(delete("/api/alarms")
                        .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andDo(print());

        verify(alarmService,times(1)).deleteAlarms(any(UserDetailsImpl.class));
    }
}