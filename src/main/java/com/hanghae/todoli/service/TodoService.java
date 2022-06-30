package com.hanghae.todoli.service;

import com.hanghae.todoli.dto.*;
import com.hanghae.todoli.models.Matching;
import com.hanghae.todoli.models.Member;
import com.hanghae.todoli.models.Todo;
import com.hanghae.todoli.repository.MatchingRepository;
import com.hanghae.todoli.models.Alarm;
import com.hanghae.todoli.models.Character;

import com.hanghae.todoli.repository.AlarmRepository;
import com.hanghae.todoli.repository.CharacterRepository;
import com.hanghae.todoli.repository.MemberRepository;
import com.hanghae.todoli.repository.TodoRepository;
import com.hanghae.todoli.security.jwt.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;
    private final MemberRepository memberRepository;

    private final MatchingRepository matchingRepository;
    private final AlarmRepository alarmRepository;

    private final CharacterRepository characterRepository;

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

        todoRepository.save(todo);
    }


    //투두 인증해주기
    @Transactional
    public TodoConfirmDto confirmTodo(Long todoId, UserDetailsImpl userDetails) {
        Todo todo = todoRepository.findById(todoId).orElseThrow(
                () -> new IllegalArgumentException("todo가 존재하지 않습니다.")
        );
        todo.setConfirmState(true);
        //todoRepository.save(todo);      // 테스트 해봐야 함.

        Alarm alarm = new Alarm();
        Date now = new Date();
        SimpleDateFormat date = new SimpleDateFormat("yyyyMMdd");
        alarm.setAlarmDate(date.format(now));
        alarm.setMember(todo.getWriter());
        alarm.setSenderId(userDetails.getMember().getId());
        alarm.setMessage(userDetails.getMember().getNickname() + "님이 확인하셨습니다.");

        alarmRepository.save(alarm);

        return TodoConfirmDto.builder()
                .todoId(todo.getId())
                .confirmState(todo.getConfirmState())
                .build();
    }

    //투두 완료
    @Transactional
    public TodoCompletionDto completionTodo(Long todoId, UserDetailsImpl userDetails) {
        Long memberId = userDetails.getMember().getId();

        Todo todo = todoRepository.findById(todoId).orElseThrow(
                () -> new IllegalArgumentException("todo가 존재하지 않습니다.")
        );
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new IllegalArgumentException("아이디가 존재하지 않습니다.")
        );

        // 투두 완료
        if (!todo.getCompletionState())
            todo.completionState();
        //todoRepository.save(todo); // 테스트 해봐야함


        Character character = member.getCharacter();
        int exp = character.getExp();
        int maxExp = character.getMaxExp();

        //난이도별 보상, 레벨업
        int difficulty = todo.getDifficulty();
        switch (difficulty) {
            case 1:
                character.setMoneyAndExp(10, 5);
                //characterRepository.save(character); // 테스트 해보기
                break;
            case 2:
                character.setMoneyAndExp(20, 10);
                break;
            case 3:
                character.setMoneyAndExp(30, 15);
                break;
            case 4:
                character.setMoneyAndExp(40, 20);
                break;
        }

        calcLevelAndExp(character, exp, maxExp);

        return TodoCompletionDto.builder()
                .todoId(todo.getId())
                .completionState(todo.getCompletionState())
                .build();
    }

    private void calcLevelAndExp(Character character, int exp, int maxExp) {
        int tmp = 0;
        if (exp >= maxExp) {
            character.levelUp();    // 레벨 올리고 exp 0 만들어준다.
            if (exp != maxExp) {
                tmp = exp - 100;
                character.zeroExp();
                character.addExp(tmp);
            }
            character.zeroExp();
        }
    }

    // 투두 삭제
    @Transactional
    public void deleteTodo(Long id, UserDetailsImpl userDetails) {
        // 로그인 유저와 작성자가 일치?
        // 불일치시 메시지
        Todo todo = todoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Todo가 존재하지 않습니다!"));

        if (!todo.getWriter().equals(userDetails.getMember())) {
            throw new IllegalArgumentException("Todo 작성자가 아닙니다!");
        }

        todoRepository.deleteById(id);
    }

    //상대방 투두 조회
    public TodoResponseDto getPairTodos(Long memberId, UserDetailsImpl userDetails) {
        Long id = userDetails.getMember().getId();
        Matching matching = matchingRepository.getMatching(id).orElseThrow(
                () -> new IllegalArgumentException("매칭되어있지 않습니다.")
        );
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
                    .difficulty(todo.getDifficulty())
                    .confirmState(todo.getConfirmState())
                    .completionState(todo.getCompletionState())
                    .build();
            todoInfoDtoList.add(todoInfoDto);
        }
        return new TodoResponseDto(matchingStateDto, todoInfoDtoList);
    }
}
