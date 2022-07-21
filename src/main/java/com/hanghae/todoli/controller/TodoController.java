package com.hanghae.todoli.controller;


import com.hanghae.todoli.dto.TodoCompletionDto;
import com.hanghae.todoli.dto.TodoConfirmDto;
import com.hanghae.todoli.dto.TodoRegisterDto;
import com.hanghae.todoli.dto.TodoResponseDto;
import com.hanghae.todoli.security.jwt.UserDetailsImpl;
import com.hanghae.todoli.service.TodoService;
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
    @ApiResponses({
            @ApiResponse(code=200, message="등록 성공"),
            @ApiResponse(code=400, message="실패"),
            @ApiResponse(code=403, message="Forbidden")
    })
    @ApiOperation(value = "투두 등록 메소드", notes = "자신이 작성한 투두를 등록하는 api 입니다.")
    @PostMapping("/todos")
    public void todoRegister(@RequestBody TodoRegisterDto registerDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        todoService.registerTodo(registerDto, userDetails);
    }

    // 투두 조회
    @ApiResponses({
            @ApiResponse(code=200, message="조회 성공"),
            @ApiResponse(code=400, message="실패"),
            @ApiResponse(code=403, message="Forbidden")
    })
    @ApiOperation(value = "투두 조회 메소드", notes = "자신이 작성한 투두를 조회하는 api 입니다.")
    @GetMapping("/mytodos")
    public TodoResponseDto getMyTodos(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return todoService.getMyTodos(userDetails);
    }

    // 투두 수정
    @ApiResponses({
            @ApiResponse(code=200, message="수정 성공"),
            @ApiResponse(code=400, message="실패"),
            @ApiResponse(code=403, message="Forbidden")
    })
    @ApiOperation(value = "투두 수정 메소드", notes = "자신이 작성한 투두를 수정하는 api 입니다.")
    @PatchMapping(value = "/todos/{todoId}")
    public void todoModify(@PathVariable Long todoId,
                           @RequestBody TodoRegisterDto registerDto,
                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        todoService.todoModify(todoId, registerDto, userDetails);
    }

    //투두 인증해주기
    @ApiResponses({
            @ApiResponse(code=200, message="인증 성공"),
            @ApiResponse(code=400, message="실패"),
            @ApiResponse(code=403, message="Forbidden")
    })
    @ApiOperation(value = "투두 인증 메소드", notes = "상대방이 작성한 투두를 인증해주는 api 입니다.")
    @PatchMapping("/todos/confirm/{todoId}")
    public String confirmTodo(@PathVariable Long todoId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try{
            todoService.confirmTodo(todoId, userDetails);
            return "인증을 완료하였습니다.";
        }catch (IllegalArgumentException e){
            return e.getMessage();
        }
    }

    //투두 완료(경험치, 돈 획득)
    @ApiResponses({
            @ApiResponse(code=200, message="완료 성공"),
            @ApiResponse(code=400, message="실패"),
            @ApiResponse(code=403, message="Forbidden")
    })
    @ApiOperation(value = "투두 완료 메소드", notes = "자신이 작성한 투두를 달성시키는 api 입니다.")
    @PatchMapping("/todos/completion/{todoId}")
    public String completionTodo(@PathVariable Long todoId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try{
            todoService.completionTodo(todoId, userDetails);
            return "완료하였습니다.";
        }catch (IllegalArgumentException e){
            return e.getMessage();
        }
    }

    // 투두 삭제
    @ApiResponses({
            @ApiResponse(code=200, message="삭제 성공"),
            @ApiResponse(code=400, message="실패"),
            @ApiResponse(code=403, message="Forbidden")
    })
    @ApiOperation(value = "투두 삭제 메소드", notes = "자신이 작성한 투두를 삭제하는 api 입니다.")
    @DeleteMapping("/todos/{id}")
    public void todoDelete(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        todoService.deleteTodo(id, userDetails);
    }

    //상대방 투두 조회
    @ApiResponses({
            @ApiResponse(code=200, message="조회 성공"),
            @ApiResponse(code=400, message="실패"),
            @ApiResponse(code=403, message="Forbidden")
    })
    @ApiOperation(value = "투두 조회 메소드", notes = "매칭된 상대가 작성한 투두를 조회하는 api 입니다.")
    @GetMapping("/todos/pair/{memberId}")
    public TodoResponseDto getPairTodos(@PathVariable Long memberId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return todoService.getPairTodos(memberId, userDetails);
    }
}
