package com.hanghae.todoli.todo.service;


import com.hanghae.todoli.alarm.Alarm;
import com.hanghae.todoli.alarm.AlarmRepository;
import com.hanghae.todoli.character.Character;
import com.hanghae.todoli.exception.CustomException;
import com.hanghae.todoli.exception.ErrorCode;
import com.hanghae.todoli.matching.Matching;
import com.hanghae.todoli.matching.MatchingRepository;
import com.hanghae.todoli.matching.dto.MatchingStatePartnerDto;
import com.hanghae.todoli.matching.dto.MatchingStateResponseDto;
import com.hanghae.todoli.member.Member;
import com.hanghae.todoli.member.MemberRepository;
import com.hanghae.todoli.security.UserDetailsImpl;
import com.hanghae.todoli.todo.dto.*;
import com.hanghae.todoli.todo.model.Todo;
import com.hanghae.todoli.todo.repository.TodoRepository;
import com.hanghae.todoli.utils.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;
    private final MemberRepository memberRepository;
    private final MatchingRepository matchingRepository;
    private final AlarmRepository alarmRepository;
    private final Validator validator;

    // 투두 등록
    @Transactional
    public String registerTodo(TodoRegisterDto registerDto, UserDetailsImpl userDetails) {

        //매칭 아닐때 매칭투두 작성 에러처리
        if (!userDetails.getMember().getMatchingState() && registerDto.getTodoType() == 2) {
            throw new CustomException(ErrorCode.NOT_MATCHED_MEMBER);
        }
        Member member = userDetails.getMember();

        validator.validTodo(registerDto);

        Todo todo = new Todo(member, registerDto);
        todoRepository.save(todo);
        return "투두 등록 성공";
    }


    //투두 인증해주기
    @Transactional
    public TodoConfirmDto confirmTodo(Long todoId, UserDetailsImpl userDetails) {
        Todo todo = getTodo(todoId);

        //파트너 아이디 구하기
        Long userId = userDetails.getMember().getId();
        Matching matching = getMatching(userId);

        Long partnerId = userId.equals(matching.getRequesterId()) ? matching.getRespondentId() : matching.getRequesterId();

        if (!todo.getWriter().getId().equals(partnerId)) {
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        }

        if (!todo.getProofImg().isEmpty() && todo.getTodoType() == 2) {
            if (todo.getConfirmState()) {
                throw new CustomException(ErrorCode.CONFIRMED_TODO);
            }
            todo.setConfirmState(true);

            List<Alarm> byTodoId = alarmRepository.findAllByTodoId(todoId);

            for (Alarm a : byTodoId) {
                a.setAlarmState(1L);
            }

            return TodoConfirmDto.builder()
                    .todoId(todo.getId())
                    .confirmState(todo.getConfirmState())
                    .build();
        } else {
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        }
    }

    //투두 완료
    @Transactional
    public TodoCompletionDto completionTodo(Long todoId, UserDetailsImpl userDetails) {
        Long memberId = userDetails.getMember().getId();

        Todo todo = getTodo(todoId);
        Member member = getMember(memberId);
        if (todo.getTodoType() == 1) {
            todo.completionState();
            //완료 날짜 삽입
            LocalDate now = LocalDate.parse(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            todo.setCompletionDate(now);

            return TodoCompletionDto.builder()
                    .todoId(todo.getId())
                    .completionState(todo.getCompletionState())
                    .build();


        } else if (todo.getTodoType() == 2) {
            if (!todo.getCompletionState() && todo.getConfirmState()) {
                todo.completionState();
            } else if (!todo.getConfirmState()) {
                throw new CustomException(ErrorCode.NOT_CONFIRMED_TODO);
            } else throw new CustomException(ErrorCode.CONFIRMED_TODO);

            Character character = member.getCharacter();

            //난이도별 보상, 레벨업
            int difficulty = todo.getDifficulty();
            character.reflectTodo(difficulty);

            //완료 날짜 삽입
            LocalDate now = LocalDate.parse(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            todo.setCompletionDate(now);

            return TodoCompletionDto.builder()
                    .todoId(todo.getId())
                    .completionState(todo.getCompletionState())
                    .build();
        } else throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
    }

    // 투두 삭제
    @Transactional
    public String deleteTodo(Long id, UserDetailsImpl userDetails) {
        Todo todo = getTodo(id);

        // 로그인 유저와 작성자가 일치확인
        if (!todo.getWriter().getId().equals(userDetails.getMember().getId())) {
            throw new CustomException(ErrorCode.NOT_TODO_WRITER);
        }
        todoRepository.deleteById(id);

        return "투두 삭제 성공";
    }

    // 투두 수정
    @Transactional
    public Todo todoModify(Long todoId, TodoModifyDto registerDto, UserDetailsImpl userDetails) {

        Todo todo = getTodo(todoId);

        Long myId = userDetails.getMember().getId();
        Member member = getMember(myId);

        if (!todo.getWriter().getId().equals(myId)) {
            throw new CustomException(ErrorCode.NOT_TODO_WRITER);
        }
        if (!member.getMatchingState() && registerDto.getTodoType() == 2) {
            throw new CustomException(ErrorCode.NOT_MATCHED_MEMBER);
        }
        if (registerDto.getContent() == null || registerDto.getContent().equals("")) {
            throw new CustomException(ErrorCode.NO_INPUT_CONTENT);
        }

        //투두 데이터
        todo.update(member, registerDto);

        return todo;
    }

    // 투두 수정 조회
    public TodoModifyDto getModifyTodo(Long todoId) {
        Todo todo = getTodo(todoId);

        return TodoModifyDto.builder()
                .content(todo.getContent())
                .difficulty(todo.getDifficulty())
                .todoType(todo.getTodoType())
                .build();

    }

    // 내 투두 목록 조회
    public TodoResponseDto getMyTodos(UserDetailsImpl userDetails) {
        Long myId = userDetails.getMember().getId();

        // 로그인중인 사용자의 매칭 정보
        Member member = memberRepository.findById(myId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));

        Boolean myMatchingState = member.getMatchingState();

        // 매칭 정보 리스트 생성
        List<MatchingStateResponseDto> matchingStates = new ArrayList<>();
        MatchingStateResponseDto stateResponseDto = new MatchingStateResponseDto(myMatchingState);
        matchingStates.add(stateResponseDto);

        // 로그인중인 사용자 id로 작성한 투두 조회
        List<TodoInfoDto> todos = getTodoInfoDtos(myId);
        return new TodoResponseDto(matchingStates, todos);
    }

    //상대방 투두 조회
    public PairTodoResponseDto getPairTodos(UserDetailsImpl userDetails) {

        Long id = userDetails.getMember().getId();

        Member member = getMember(id);
        Boolean myMatchingState = member.getMatchingState();

        // 로그인 중인 사용자의 매칭 여부 판단
        Matching matching = getMatching(id);

        // 매칭중인 파트너의 Long id
        Long partnerId = id.equals(matching.getRequesterId())
                ? matching.getRespondentId() : matching.getRequesterId();

        // 나의 매칭 상태, 파트너 id를 담는 List 생성
        List<MatchingStatePartnerDto> matchingStatePartnerDtos = new ArrayList<>();
        MatchingStatePartnerDto matchingStatePartnerDto = new MatchingStatePartnerDto(myMatchingState, partnerId);

        matchingStatePartnerDtos.add(matchingStatePartnerDto);

        // 파트너의 투두 내용 List 생성
        List<TodoInfoDto> todos = getTodoInfoDtos(partnerId);

        // 생성된 List 반환
        return new PairTodoResponseDto(matchingStatePartnerDtos, todos);
    }

    private List<TodoInfoDto> getTodoInfoDtos(Long id) {
        List<TodoInfoDto> todoInfoDtoList = new ArrayList<>();
        List<Todo> todos = todoRepository.findAllByWriterIdOrderByIdDesc(id);
        for (Todo todo : todos) {
            TodoInfoDto todoInfoDto = TodoInfoDto.builder()
                    .todoId(todo.getId())
                    .content(todo.getContent())
                    .proofImg(todo.getProofImg())
                    .startDate(todo.getStartDate())
                    .endDate(todo.getEndDate())
                    .confirmDate(todo.getConfirmDate())
                    .difficulty(todo.getDifficulty())
                    .confirmState(todo.getConfirmState())
                    .completionState(todo.getCompletionState())
                    .todoType(todo.getTodoType())
                    .createdAt(todo.getCreatedAt())
                    .build();
            todoInfoDtoList.add(todoInfoDto);
        }
        return todoInfoDtoList;
    }

    private Matching getMatching(Long userId) {
        return matchingRepository.getMatching(userId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_MATCHING));
    }

    private Todo getTodo(Long todoId) {
        return todoRepository.findById(todoId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_TODO));
    }

    private Member getMember(Long id) {
        return memberRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_MEMBER)
        );
    }
}