package com.hanghae.todoli.todo.service;


import com.hanghae.todoli.alarm.Alarm;
import com.hanghae.todoli.alarm.AlarmRepository;
import com.hanghae.todoli.character.Character;
import com.hanghae.todoli.exception.CustomException;
import com.hanghae.todoli.exception.ErrorCode;
import com.hanghae.todoli.matching.Matching;
import com.hanghae.todoli.matching.MatchingRepository;
import com.hanghae.todoli.matching.MatchingStatePartnerDto;
import com.hanghae.todoli.matching.MatchingStateResponseDto;
import com.hanghae.todoli.member.Member;
import com.hanghae.todoli.member.MemberRepository;
import com.hanghae.todoli.security.UserDetailsImpl;
import com.hanghae.todoli.todo.model.Todo;
import com.hanghae.todoli.todo.repository.TodoRepository;
import com.hanghae.todoli.todo.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TodoService {

    /**
     * 투두 등록
     * - 로그인 중인 회원 정보 가져와서 작성자 정보에 입력
     * - 매칭 아이디가 false면 투두 작성 불가 -> '파트너를 매칭하세요!' 메시지 return
     * - 작성자 정보 중 매칭 상태 저장
     * <p>
     * 투두 조회
     * - 투두 작성자
     * - 작성자와 매칭중인 사용자만 볼 수 있도록
     * - 매칭 번호가 작성자의 매칭 번호와 일치 하는지 확인
     * - 작성자 정보 중 매칭 상태 포함 return
     * <p>
     * 투두 완료 처리
     * -
     * <p>
     * 투두 삭제
     * - 투두 작성자와 로그인 유저가 일치
     * - 일치 -> 삭제
     * - 불일치 -> '투두 작성자가 아닙니다!'
     */

    private final TodoRepository todoRepository;

    private final MemberRepository memberRepository;

    private final MatchingRepository matchingRepository;

    private final AlarmRepository alarmRepository;

    // 투두 등록
    @Transactional
    public void registerTodo(TodoRegisterDto registerDto, UserDetailsImpl userDetails) {

        //매칭 아닐때 매칭투두 작성 에러처리
        if (!userDetails.getMember().getMatchingState() && registerDto.getTodoType() == 2) {
            throw new CustomException(ErrorCode.NOT_MATCHED_MEMBER);
        }

        Member member = userDetails.getMember();
        validator(registerDto);

        // 새로운 투두
        Todo todo = new Todo(member, registerDto);
        todoRepository.save(todo);
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
            Alarm byTodoId = alarmRepository.findByTodoId(todoId);
            byTodoId.setAlarmState(1L);

            //알림 보내기 추후에 추가기능으로 열 수 있음
//            Alarm alarm = new Alarm();
//            LocalDate now = LocalDate.parse(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
//            alarm.setAlarmDate(now);
//            alarm.setMember(todo.getWriter());
//            alarm.setSenderId(userDetails.getMember().getId());
//            alarm.setMessage(userDetails.getMember().getNickname() + "님이 인증하셨습니다.");
//            alarmRepository.save(alarm);

            return TodoConfirmDto.builder().todoId(todo.getId()).confirmState(todo.getConfirmState()).build();
        } else {
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        }

    }

    private Matching getMatching(Long userId) {
        Matching matching = matchingRepository.getMatching(userId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_MATCHING));
        return matching;
    }

    private Todo getTodo(Long todoId) {
        Todo todo = todoRepository.findById(todoId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_TODO));
        return todo;
    }

    //투두 완료
    @Transactional
    public TodoCompletionDto completionTodo(Long todoId, UserDetailsImpl userDetails) {
        Long memberId = userDetails.getMember().getId();

        Todo todo = getTodo(todoId);
        Member member = getMember(memberId);

        // 투두 완료
        if (!todo.getCompletionState() && todo.getConfirmState()) {
            todo.completionState();
            //todoRepository.save(todo);    // 테스트 필요
        } else {
            throw new CustomException(ErrorCode.NOT_CONFIRMED_TODO);
        }

        Character character = member.getCharacter();
        int exp = 0;

        //난이도별 보상, 레벨업
        int difficulty = todo.getDifficulty();
        switch (difficulty) {
            case 1:
                character.setMoney(10);
                //characterRepository.save(character); // 테스트 해보기
                exp = 5;
                break;
            case 2:
                character.setMoney(20);
                exp = 10;
                break;
            case 3:
                character.setMoney(30);
                exp = 15;
                break;
            case 4:
                character.setMoney(40);
                exp = 20;
                break;
        }

        calcLevelAndExp(character, exp);

        return TodoCompletionDto.builder()
                .todoId(todo.getId())
                .completionState(todo.getCompletionState())
                .build();
    }

    private void calcLevelAndExp(Character character, int exp) {
        character.editExp(exp);
    }

    // 투두 삭제
    @Transactional
    public void deleteTodo(Long id, UserDetailsImpl userDetails) {
        // 로그인 유저와 작성자가 일치?
        // 불일치시 메시지
        Todo todo = getTodo(id);

        if (!todo.getWriter().getId().equals(userDetails.getMember().getId())) {
            throw new CustomException(ErrorCode.NOT_TODO_WRITER);
        }
        todoRepository.deleteById(id);
    }

    //상대방 투두 조회
    //  TODO : 2022-07-08 파트너 투두 조회 수정( 수정 완료 / 테스트 완료 )
    //      1. 로그인한 유저의 매칭 상태 검사
    //      2. 매칭중이 아니라면 매칭중이지 않다는 에러 메시지
    //      3. 매칭중이라면 매칭중인 파트너 아이디 조회
    //      4. 투두에 저장되어있는 작정자가 파트너 아이디인 투두 조회
    public PairTodoResponseDto getPairTodos(UserDetailsImpl userDetails) {
        // 로그인 중인 사용자의 Long id
        Long id = userDetails.getMember().getId();

        // 로그인 중인 사용자의 매칭 여부 판단
        Matching matching = getMatching(id);

        // 나의 매칭 상태 조회

        Member member = getMember(id);

        Boolean matchingState = member.getMatchingState();

        // 매칭중인 파트너의 Long id
        Long partnerId = id.equals(matching.getRequesterId()) ? matching.getRespondentId() : matching.getRequesterId();

        // 나의 매칭 상태, 파트너 id를 담는 List 생성
        List<MatchingStatePartnerDto> matchingStatePartnerDtos = new ArrayList<>();
        MatchingStatePartnerDto matchingStatePartnerDto = new MatchingStatePartnerDto(matchingState, partnerId);

        matchingStatePartnerDtos.add(matchingStatePartnerDto);

        // 파트너의 투두 내용 List 생성
        List<TodoInfoDto> todoInfoDtoList = getTodoInfoDtos(partnerId);

        // 생성된 List 반환
        return new PairTodoResponseDto(matchingStatePartnerDtos, todoInfoDtoList);
    }



    private Member getMember(Long id) {
        Member member = memberRepository.findById(id).orElseThrow(
                ()-> new CustomException(ErrorCode.NOT_FOUND_MEMBER)
        );
        return member;
    }

    // 내 투두 목록 조회
    public TodoResponseDto getMyTodos(UserDetailsImpl userDetails) {
        // 로그인중인 사용자 id 가져오기
        Long myId = userDetails.getMember().getId();

        // 로그인중인 사용자의 매칭 정보
        Boolean loggedMemberMatchingState = memberRepository.findById(myId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_MEMBER)
        ).getMatchingState();

        // 매칭 정보 리스트 생성
        List<MatchingStateResponseDto> matchingStates = new ArrayList<>();
        MatchingStateResponseDto stateResponseDto = new MatchingStateResponseDto(loggedMemberMatchingState);
        matchingStates.add(stateResponseDto);

        // 로그인중인 사용자 id로 작성한 투두 조회
        List<TodoInfoDto> todoInfoDtoList = getTodoInfoDtos(myId);
        return new TodoResponseDto(matchingStates, todoInfoDtoList);
    }

    // 투두 수정
    @Transactional
    public void todoModify(Long todoId, TodoRegisterDto registerDto, UserDetailsImpl userDetails) {
        // 투두 유무 확인
        Todo todo = getTodo(todoId);

        // 로그인중인 사용자 id 가져오기
        Long myId = userDetails.getMember().getId();
        Member member = getMember(myId);

        if (!todo.getWriter().getId().equals(myId)) {
            throw new CustomException(ErrorCode.NOT_TODO_WRITER);
        }
        //투두 데이터
        todo.update(member, registerDto);
    }

    private void validator(TodoRegisterDto registerDto) {
        LocalDate now = LocalDate.parse(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        if (registerDto.getStartDate() == null)
            throw new CustomException(ErrorCode.NO_INPUT_START_DATE);
        if (registerDto.getEndDate() == null)
            throw new CustomException(ErrorCode.NO_INPUT_END_DATE);
        if (registerDto.getContent() == null)
            throw new CustomException(ErrorCode.NO_INPUT_CONTENT);
        if (registerDto.getDifficulty() == 0)
            throw new CustomException(ErrorCode.NO_INPUT_DIFFICULTY);
        if (registerDto.getTodoType() == 0)
            throw new CustomException(ErrorCode.NO_INPUT_TODO_TYPE);
        if (registerDto.getStartDate().isBefore(now))
            throw new CustomException(ErrorCode.FORBIDDEN_START_DATE);
        if (registerDto.getEndDate().isBefore(registerDto.getStartDate()))
            throw new CustomException(ErrorCode.FORBIDDEN_END_DATE);
    }

    private List<TodoInfoDto> getTodoInfoDtos(Long partnerId) {
        List<TodoInfoDto> todoInfoDtoList = new ArrayList<>();
        List<Todo> todos = todoRepository.findAllByWriterIdOrderByIdDesc(partnerId);
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
}
