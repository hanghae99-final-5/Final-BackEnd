package com.hanghae.todoli.todo.controller;

import com.hanghae.todoli.security.UserDetailsImpl;
import com.hanghae.todoli.todo.dto.*;
import com.hanghae.todoli.todo.service.StatisticsService;
import com.hanghae.todoli.todo.service.TodoService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
    private final StatisticsService statisticsService;


    // 투두 등록
    @ApiResponses({
            @ApiResponse(code = 200, message = "등록 성공"),
            @ApiResponse(code = 400, message = "실패"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @ApiOperation(value = "투두 등록 메소드", notes = "자신이 작성한 투두를 등록하는 api 입니다.")
    @PostMapping("/todos")
    public void todoRegister(@RequestBody TodoRegisterDto registerDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        todoService.registerTodo(registerDto, userDetails);
    }

    // 투두 조회
    @ApiResponses({
            @ApiResponse(code = 200, message = "조회 성공"),
            @ApiResponse(code = 400, message = "실패"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @ApiOperation(value = "투두 조회 메소드", notes = "자신이 작성한 투두를 조회하는 api 입니다.")
    @GetMapping("/mytodos")
    public TodoResponseDto getMyTodos(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return todoService.getMyTodos(userDetails);
    }

    // 투두 수정 조회
    @GetMapping("/todos/{todoId}")
    public TodoModifyDto getModifyTodo(@PathVariable Long todoId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return todoService.getModifyTodo(todoId);
    }

    // 투두 수정
    @ApiResponses({
            @ApiResponse(code = 200, message = "수정 성공"),
            @ApiResponse(code = 400, message = "실패"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @ApiOperation(value = "투두 수정 메소드", notes = "자신이 작성한 투두를 수정하는 api 입니다.")
    @PatchMapping(value = "/todos/{todoId}")
    public void todoModify(@PathVariable Long todoId,
                           @RequestBody TodoModifyDto registerDto,
                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        todoService.todoModify(todoId, registerDto, userDetails);
    }

    //투두 인증해주기
    @ApiResponses({
            @ApiResponse(code = 200, message = "인증 성공"),
            @ApiResponse(code = 400, message = "실패"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @ApiOperation(value = "투두 인증 메소드", notes = "상대방이 작성한 투두를 인증해주는 api 입니다.")
    @PatchMapping("/todos/confirm/{todoId}")
    public String confirmTodo(@PathVariable Long todoId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        todoService.confirmTodo(todoId, userDetails);
        return "인증을 완료하였습니다.";
    }

    //투두 완료(경험치, 돈 획득)
    @ApiResponses({
            @ApiResponse(code = 200, message = "완료 성공"),
            @ApiResponse(code = 400, message = "실패"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @ApiOperation(value = "투두 완료 메소드", notes = "자신이 작성한 투두를 달성시키는 api 입니다.")
    @PatchMapping("/todos/completion/{todoId}")
    public String completionTodo(@PathVariable Long todoId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        todoService.completionTodo(todoId, userDetails);
        return "수고하셨습니다.";
    }

    // 투두 삭제
    @ApiResponses({
            @ApiResponse(code = 200, message = "삭제 성공"),
            @ApiResponse(code = 400, message = "실패"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @ApiOperation(value = "투두 삭제 메소드", notes = "자신이 작성한 투두를 삭제하는 api 입니다.")
    @DeleteMapping("/todos/{id}")
    public void todoDelete(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        todoService.deleteTodo(id, userDetails);
    }

    //상대방 투두조회
    @ApiResponses({
            @ApiResponse(code = 200, message = "조회 성공"),
            @ApiResponse(code = 400, message = "실패"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @ApiOperation(value = "투두 조회 메소드", notes = "매칭된 상대가 작성한 투두를 조회하는 api 입니다.")
    @GetMapping("/todos/pair")
    public PairTodoResponseDto getPairTodos(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return todoService.getPairTodos(userDetails);
    }

    //일간 통계
    @ApiResponses({
            @ApiResponse(code = 200, message = "조회 성공"),
            @ApiResponse(code = 400, message = "실패"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @ApiOperation(value = "투두 조회 메소드", notes = "7일 전 까지의 사용자 정보(투두 달성률, 얻은 exp)를 조회하는 api 입니다.")
    @GetMapping("/statistics/daily")
    public StatisticsResponseDto getStatisticsDaily(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return statisticsService.getStatisticsDaily(userDetails);
    }

    //월간 통계
    @ApiResponses({
            @ApiResponse(code = 200, message = "조회 성공"),
            @ApiResponse(code = 400, message = "실패"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @ApiOperation(value = "투두 조회 메소드", notes = "6개월 전 까지의 사용자 정보(투두 달성률, 얻은 exp)를 조회하는 api 입니다.")
    @GetMapping("/statistics/monthly")
    public StatisticsResponseDto getStatisticsMonthly(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return statisticsService.getStatisticsMonthly(userDetails);
    }

    //주간 통계
    @ApiResponses({
            @ApiResponse(code = 200, message = "조회 성공"),
            @ApiResponse(code = 400, message = "실패"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @ApiOperation(value = "투두 조회 메소드", notes = "5주 전 까지의 사용자 정보(투두 달성률, 얻은 exp)를 조회하는 api 입니다.")
    @GetMapping("/statistics/weekly")
    public StatisticsResponseDto getStatisticsWeekly(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return statisticsService.getStatisticsWeekly(userDetails);
    }
}
