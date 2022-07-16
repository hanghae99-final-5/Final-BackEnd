package com.hanghae.todoli.todo.controller;


import com.hanghae.todoli.security.UserDetailsImpl;
import com.hanghae.todoli.todo.dto.PairTodoResponseDto;
import com.hanghae.todoli.todo.dto.TodoModifyDto;
import com.hanghae.todoli.todo.dto.TodoRegisterDto;
import com.hanghae.todoli.todo.dto.TodoResponseDto;
import com.hanghae.todoli.todo.service.TodoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TodoController {

    /**
     * 투두 등록
     * - 로그인 중인 회원 정보 가져와서 작성자 정보에 입력
     * - 매칭 아이디가 false면 투두 작성 불가 -> '파트너를 매칭하세요!' 메시지 return
     * - 작성자 정보 중 매칭 번호 저장
     * <p>
     * 투두 조회
     * - 투두 작성자
     * - 작성자와 매칭중인 사용자만 볼 수 있도록
     * - 매칭 번호가 작성자의 매칭 번호와 일치 하는지 확인
     * <p>
     * 투두 완료 처리
     * -
     * <p>
     * 사진 등록 및 재등록
     * - 사진 등록시 인증일 = 종료일 + 3 으로 설정
     * <p>
     * 투두 삭제
     * - 투두 작성자와 로그인 유저가 일치
     * - 일치 -> 삭제
     * - 불일치 -> '투두 작성자가 아닙니다!'
     */

    private final TodoService todoService;


    // 투두 등록
    @PostMapping("/todos")
    public void todoRegister(@RequestBody TodoRegisterDto registerDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        todoService.registerTodo(registerDto, userDetails);
    }

    // 투두 조회
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
    @PatchMapping(value = "/todos/{todoId}")
    public void todoModify(@PathVariable Long todoId,
                           @RequestBody TodoModifyDto registerDto,
                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        todoService.todoModify(todoId, registerDto, userDetails);
    }

    //투두 인증해주기
    @PatchMapping("/todos/confirm/{todoId}")
    public String confirmTodo(@PathVariable Long todoId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        todoService.confirmTodo(todoId, userDetails);
        return "인증을 완료하였습니다.";
    }

    //투두 완료(경험치, 돈 획득)
    @PatchMapping("/todos/completion/{todoId}")
    public String completionTodo(@PathVariable Long todoId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        todoService.completionTodo(todoId, userDetails);
        return "수고하셨습니다.";
    }

    // 투두 삭제
    @DeleteMapping("/todos/{id}")
    public void todoDelete(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        todoService.deleteTodo(id, userDetails);
    }

    //상대방 투두 조회
    //  TODO : 파트너 투두 조회
    //      1. 로그인한 유저의 매칭 상태 검사
    //      2. 매칭중이 아니라면 매칭중이지 않다는 에러 메시지
    //      3. 매칭중이라면 매칭중인 파트너 아이디 조회
    //      4. 투두에 저장되어있는 작정자가 파트너 아이디인 투두 조회
    @GetMapping("/todos/pair")
    public PairTodoResponseDto getPairTodos(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return todoService.getPairTodos(userDetails);
    }
//    @GetMapping("/todos/pair")
//    public TodoResponseDto getPairTodos(@PathVariable Long memberId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
//        return todoService.getPairTodos(memberId, userDetails);
//    }
}
