package com.hanghae.todoli.todo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae.todoli.member.Member;
import com.hanghae.todoli.security.MockSpringSecurityFilter;
import com.hanghae.todoli.security.SecurityConfig;
import com.hanghae.todoli.security.UserDetailsImpl;
import com.hanghae.todoli.todo.dto.ProofImgRequestDto;
import com.hanghae.todoli.todo.service.ProofImgService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProofImgController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = SecurityConfig.class
                )})
@MockBean(JpaMetamodelMappingContext.class)
class ProofImgControllerTest {

    private MockMvc mvc;
    private Principal mockPrincipal;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    ProofImgService imgService;

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
    @DisplayName("투두 인증 사진 등록")
    void proofImgRegister() throws Exception {

        //given
        this.mockUserSetup(); // 로그인처리

        MockMultipartFile file = new MockMultipartFile(
                "test1",
                "test1.PNG",
                "multipart/form-data",
                "file".getBytes());

        ProofImgRequestDto proofImgRequestDto = new ProofImgRequestDto(file);

        //when - then
        mvc.perform(
                patch("/api/proofimgs/{todoId}",1)
                        .content(proofImgRequestDto.toString())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andDo(print());

        verify(imgService,times(1))
                .imgRegister(anyLong(),any(ProofImgRequestDto.class),any(UserDetailsImpl.class));
    }
}