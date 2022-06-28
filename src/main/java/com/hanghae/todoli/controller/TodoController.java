package com.hanghae.todoli.controller;

import com.hanghae.todoli.dto.TodoRequestDto;
import com.hanghae.todoli.security.jwt.UserDetailsImpl;
import com.hanghae.todoli.service.TodoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    // 투두 등록
    @PostMapping("/todos")
    public void todoRegister(@RequestBody TodoRequestDto requestDto,
                             @AuthenticationPrincipal UserDetailsImpl userDetails) {

        todoService.registerTodo(requestDto, userDetails);
    }
}
