package com.hanghae.todoli.controller;

import com.hanghae.todoli.dto.TodoCompletionDto;
import com.hanghae.todoli.dto.TodoConfirmDto;
import com.hanghae.todoli.dto.TodoRequestDto;
import com.hanghae.todoli.security.jwt.UserDetailsImpl;
import com.hanghae.todoli.service.TodoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TodoController {

    /**
     * 투두 등록
     * - 매칭 아이디가 null이면 투두 작성 불가 -> '파트너를 매칭하세요!' 메시지 return
     * - 로그인 중인 회원 정보 가져와서 작성자 정보에 입력
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
     * 투두 삭제
     * - 투두 작성자와 로그인 유저가 일치
     * - 일치 -> 삭제
     * - 불일치 -> '투두 작성자가 아닙니다!'
     */

    private final TodoService todoService;

    // 투두 등록
    @PostMapping("/todos")
    public void todoRegister(@RequestBody TodoRequestDto requestDto,
                             @AuthenticationPrincipal UserDetailsImpl userDetails) {
        todoService.registerTodo(requestDto, userDetails);
    }

    //투두 인증해주기
    @PatchMapping("/api/todos/confirm/{todoId}")
    public TodoConfirmDto confirmTodo(@PathVariable Long todoId,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails){
        return todoService.confirmTodo(todoId, userDetails);
    }

    //투두 완료(경험치, 돈 획득)
    @PatchMapping("/api/todos/complition/{todoId}")
    public TodoCompletionDto completionTodo(@PathVariable Long todoId,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails){
        return todoService.completionTodo(todoId, userDetails);

    // 투두 삭제
    @DeleteMapping("/todos/{id}")
    public void todoDelete(@PathVariable Long id,
                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        todoService.deleteTodo(id, userDetails);

    }
}
