package com.hanghae.todoli.character;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CharacterController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = SecurityConfig.class
                )})
@MockBean(JpaMetamodelMappingContext.class)
class CharacterControllerTest {

    @MockBean
    CharacterService characterService;

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
    @DisplayName("캐릭터 상태 조회")
    void getCharState() throws Exception{
        this.mockUserSetup();

        mvc.perform(get("/api/characters")
                        .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andDo(print());

        verify(characterService,times(1))
                .getCharState(any(UserDetailsImpl.class));
    }

    @Test
    @DisplayName("상대방 캐릭터 상태 조회")
    void getPartnerState() throws Exception{
        this.mockUserSetup();

        mvc.perform(get("/api/characters/partners")
                        .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andDo(print());

        verify(characterService,times(1))
                .getPartnerState(any(UserDetailsImpl.class));
    }

    @Test
    @DisplayName("푸터용 자신, 상대방 캐릭터")
    void getCharacterInFooter() throws Exception{
        this.mockUserSetup();

        mvc.perform(get("/api/characters/footer")
                        .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andDo(print());

        verify(characterService,times(1))
                .getCharacterInFooter(any(UserDetailsImpl.class));
    }
}