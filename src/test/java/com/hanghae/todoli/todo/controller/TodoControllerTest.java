package com.hanghae.todoli.todo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae.todoli.member.Member;
import com.hanghae.todoli.security.MockSpringSecurityFilter;
import com.hanghae.todoli.security.SecurityConfig;
import com.hanghae.todoli.security.UserDetailsImpl;
import com.hanghae.todoli.todo.dto.TodoModifyDto;
import com.hanghae.todoli.todo.dto.TodoRegisterDto;
import com.hanghae.todoli.todo.service.TodoService;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;
import java.time.LocalDate;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TodoController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = SecurityConfig.class
                )})
@MockBean(JpaMetamodelMappingContext.class)
class TodoControllerTest {

    @MockBean
    TodoService todoService;

    private MockMvc mvc;
    private Principal mockPrincipal;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

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
    @DisplayName("투두 등록")
    void todoRegister() throws Exception {

        //given
        this.mockUserSetup(); // 로그인처리

        TodoRegisterDto todoRegisterDto = TodoRegisterDto.builder()
                .content("작성 테스트")
                .startDate(LocalDate.ofEpochDay(2022 - 07 - 20))
                .endDate(LocalDate.ofEpochDay(2022 - 07 - 21))
                .difficulty(3)
                .todoType(1)
                .build();

        String content = objectMapper.writeValueAsString(todoRegisterDto);

        //when - then
        mvc.perform(post("/api/todos")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("투두 조회")
    void getMyTodos() throws Exception{

        //given
        this.mockUserSetup(); // 로그인처리

        //when - then
        mvc.perform(get("/api/mytodos")
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("투두 수정 조회")
    void getModifyTodo() throws Exception{

        //given
        this.mockUserSetup(); // 로그인처리

        //when - then
        mvc.perform(get("/api/todos/{todoId}",
                        1)
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("투두 수정")
    void todoModify() throws Exception{

        //given
        this.mockUserSetup(); // 로그인처리

        TodoModifyDto todoModifyDto = TodoModifyDto.builder()
                .content("투두 수정")
                .difficulty(3)
                .todoType(1)
                .build();

        String content = objectMapper.writeValueAsString(todoModifyDto);

        //when - then
        mvc.perform(patch("/api/todos/{todoId}",
                        1)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("투두 인증해주기")
    void confirmTodo() throws Exception{

        //given
        this.mockUserSetup(); // 로그인처리

        //when - then
        mvc.perform(patch("/api/todos/confirm/{todoId}",
                        1)
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("투두 완료")
    void completionTodo() throws Exception{

        //given
        this.mockUserSetup(); // 로그인처리

        //when - then
        mvc.perform(patch("/api/todos/completion/{todoId}",
                        1)
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("투두 삭제")
    void todoDelete() throws Exception{

        //given
        this.mockUserSetup(); // 로그인처리

        //when - then
        mvc.perform(delete("/api/todos/{id}",
                        1)
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("상대방 투두 조회")
    void getPairTodos() throws Exception{

        //given
        this.mockUserSetup(); // 로그인처리

        //when - then
        mvc.perform(get("/api/todos/pair")
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }
}