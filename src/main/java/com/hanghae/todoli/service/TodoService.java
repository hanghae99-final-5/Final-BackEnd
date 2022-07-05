package com.hanghae.todoli.service;


import com.hanghae.todoli.dto.*;
import com.hanghae.todoli.models.Character;
import com.hanghae.todoli.models.*;
import com.hanghae.todoli.repository.AlarmRepository;
import com.hanghae.todoli.repository.MatchingRepository;
import com.hanghae.todoli.repository.MemberRepository;
import com.hanghae.todoli.repository.TodoRepository;
import com.hanghae.todoli.security.jwt.UserDetailsImpl;
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

        // 작성자 정보
        final Member member = userDetails.getMember();

        // 새로운 투두
        final Todo todo = new Todo();

        // 투두 데이터
        todo.setWriter(member);
        todo.setContent(registerDto.getContent());
        todo.setStartDate(registerDto.getStartDate());
        todo.setEndDate(registerDto.getEndDate());
        todo.setDifficulty(registerDto.getDifficulty());
        todo.setConfirmDate(registerDto.getEndDate());
        todo.setTodoType(registerDto.getTodoType());

        todoRepository.save(todo);
    }


    //투두 인증해주기
    @Transactional
    public TodoConfirmDto confirmTodo(Long todoId, UserDetailsImpl userDetails) {
        Todo todo = todoRepository.findById(todoId).orElseThrow(() -> new IllegalArgumentException("Todo가 존재하지 않습니다."));
        //파트너 아이디 구하기
        Long userId = userDetails.getMember().getId();
        Matching matching = matchingRepository.getMatching(userId).orElseThrow(() -> new IllegalArgumentException("매칭된 상대가 존재하지 않습니다."));
        Long partnerId = userId.equals(matching.getRequesterId()) ? matching.getRespondentId() : matching.getRequesterId();

        if (!todo.getWriter().getId().equals(partnerId)) {
            throw new IllegalArgumentException("잘못된 접근입니다.");
        }

        if (!todo.getProofImg().isEmpty() && todo.getTodoType() == 1) {
            if (todo.getConfirmState()) {
                throw new IllegalArgumentException("이미 인증하였습니다.");
            }
            todo.setConfirmState(true);
            //todoRepository.save(todo);    // 테스트 필요

            Alarm alarm = new Alarm();
//        Date now = new Date();
//        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
            LocalDate now = LocalDate.parse(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            alarm.setAlarmDate(now);
            alarm.setMember(todo.getWriter());
            alarm.setSenderId(userDetails.getMember().getId());
            alarm.setMessage(userDetails.getMember().getNickname() + "님이 인증하셨습니다.");

            alarmRepository.save(alarm);

            return TodoConfirmDto.builder().todoId(todo.getId()).confirmState(todo.getConfirmState()).build();
        } else {
            throw new IllegalArgumentException("상대방이 인증하지 않았습니다.");
        }

    }

    //투두 완료
    @Transactional
    public TodoCompletionDto completionTodo(Long todoId, UserDetailsImpl userDetails) {
        Long memberId = userDetails.getMember().getId();

        Todo todo = todoRepository.findById(todoId).orElseThrow(() -> new IllegalArgumentException("Todo가 존재하지 않습니다."));
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("아이디가 존재하지 않습니다."));

        // 투두 완료
        if (!todo.getCompletionState() && todo.getConfirmState()) {
            todo.completionState();
            //todoRepository.save(todo);    // 테스트 필요
        } else {
            throw new IllegalArgumentException("파트너에게 인증을 받아주세요.");
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
        Todo todo = todoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Todo가 존재하지 않습니다!"));

        if (!todo.getWriter().getId().equals(userDetails.getMember().getId())) {
            throw new IllegalArgumentException("Todo 작성자가 아닙니다!");
        }
        todoRepository.deleteById(id);
    }

    //상대방 투두 조회
    public TodoResponseDto getPairTodos(Long memberId, UserDetailsImpl userDetails) {
        Long id = userDetails.getMember().getId();
        Matching matching = matchingRepository.getMatching(id).orElseThrow(() -> new IllegalArgumentException("매칭되어있지 않습니다."));
        Long partnerId = id.equals(matching.getRequesterId()) ? matching.getRespondentId() : matching.getRequesterId();
        if (!memberId.equals(partnerId)) {
            throw new IllegalArgumentException("매칭되어있는 상대가 아닙니다.");
        }
        Member member = memberRepository.findById(id).orElse(null);
        Boolean matchingState = member.getMatchingState();

        List<MatchingStateResponseDto> matchingStateDto = new ArrayList<>();
        MatchingStateResponseDto matchingStateResponseDto = new MatchingStateResponseDto(matchingState);
        matchingStateDto.add(matchingStateResponseDto);

        List<TodoInfoDto> todoInfoDtoList = new ArrayList<>();
        List<Todo> todos = todoRepository.findAllByWriterId(memberId);
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
                    .build();
            todoInfoDtoList.add(todoInfoDto);
        }
        return new TodoResponseDto(matchingStateDto, todoInfoDtoList);
    }

    // 내 투두 목록 조회
    public TodoResponseDto getMyTodos(UserDetailsImpl userDetails) {
        // 로그인중인 사용자 id 가져오기
        Long myId = userDetails.getMember().getId();

        // 로그인중인 사용자의 매칭 정보
        Member loggedInMember = memberRepository.findById(myId).orElseThrow(
                () -> new IllegalArgumentException("사용자 정보가 존재하지 않습니다.")
        );

        Boolean loggedMemberMatchingState = memberRepository.findById(myId).orElseThrow(
                () -> new IllegalArgumentException("사용자 정보가 존재하지 않습니다.")
        ).getMatchingState();

        // 매칭 정보 리스트 생성
        List<MatchingStateResponseDto> matchingStates = new ArrayList<>();
        MatchingStateResponseDto stateResponseDto = new MatchingStateResponseDto(loggedMemberMatchingState);
        matchingStates.add(stateResponseDto);

        // 로그인중인 사용자 id로 작성한 투두 조회
        List<TodoInfoDto> todoInfoList = new ArrayList<>();
        List<Todo> todos = todoRepository.findAllByWriterId(myId);
        for (Todo todo : todos) {
            TodoInfoDto todoInfoDto = TodoInfoDto.builder()
                    .todoId(todo.getId())
                    .content(todo.getContent())
                    .proofImg(todo.getProofImg())
                    .startDate(todo.getStartDate())
                    .endDate(todo.getEndDate())
                    .difficulty(todo.getDifficulty())
                    .confirmState(todo.getConfirmState())
                    .completionState(todo.getCompletionState())
                    .confirmDate(todo.getConfirmDate())
                    .build();
            todoInfoList.add(todoInfoDto);
        }
        return new TodoResponseDto(matchingStates, todoInfoList);
    }

    // 투두 수정
    @Transactional
    public void todoModify(Long todoId, TodoRegisterDto registerDto, UserDetailsImpl userDetails) {
        // 투두 유무 확인
        Todo todo = todoRepository.findById(todoId).orElseThrow(() -> new IllegalArgumentException("Todo가 존재하지 않습니다."));

        // 로그인중인 사용자 id 가져오기
        Long myId = userDetails.getMember().getId();
        Member member = memberRepository.findById(myId).orElseThrow(() -> new IllegalArgumentException("작성자가 아닙니다."));

        // 투두 데이터
        todo.setWriter(member);
        todo.setContent(registerDto.getContent());
        todo.setStartDate(registerDto.getStartDate());
        todo.setEndDate(registerDto.getEndDate());
        todo.setDifficulty(registerDto.getDifficulty());
        todo.setConfirmDate(registerDto.getEndDate());
        todo.setTodoType(registerDto.getTodoType());
    }
}
