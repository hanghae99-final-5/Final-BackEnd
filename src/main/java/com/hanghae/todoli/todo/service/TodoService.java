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
    public String registerTodo(TodoRegisterDto registerDto, UserDetailsImpl userDetails) {

        //매칭 아닐때 매칭투두 작성 에러처리
        if (!userDetails.getMember().getMatchingState() && registerDto.getTodoType() == 2) {
            throw new CustomException(ErrorCode.NOT_MATCHED_MEMBER);
        }

        Member member = userDetails.getMember();
        validator(registerDto);

        // 새로운 투두
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

            // TODO: 알림 보내기 추후에 추가기능으로 열 수 있음
//            Alarm alarm = new Alarm();
//            LocalDate now = LocalDate.parse(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
//            alarm.setAlarmDate(now);
//            alarm.setMember(todo.getWriter());
//            alarm.setSenderId(userDetails.getMember().getId());
//            alarm.setMessage(userDetails.getMember().getNickname() + "님이 인증하셨습니다.");
//            alarmRepository.save(alarm);

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
            int exp = 0;

            //난이도별 보상, 레벨업
            int difficulty = todo.getDifficulty();
            switch (difficulty) {
                case 1:
                    character.setMoney(10);
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
        // 로그인 유저와 작성자가 일치?
        // 불일치시 메시지
        Todo todo = getTodo(id);

        if (!todo.getWriter().getId().equals(userDetails.getMember().getId())) {
            throw new CustomException(ErrorCode.NOT_TODO_WRITER);
        }
        todoRepository.deleteById(id);

        return "투두 삭제 성공";
    }

    //상대방 투두 조회
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
    public Todo todoModify(Long todoId, TodoModifyDto registerDto, UserDetailsImpl userDetails) {
        // 투두 유무 확인
        Todo todo = getTodo(todoId);

        // 로그인중인 사용자 id 가져오기
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

    private Matching getMatching(Long userId) {
        return matchingRepository.getMatching(userId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_MATCHING));
    }

    private Todo getTodo(Long todoId) {
        return todoRepository.findById(todoId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_TODO));
    }

    private void calcLevelAndExp(Character character, int exp) {
        character.editExp(exp);
    }

    private Member getMember(Long id) {
        return memberRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_MEMBER)
        );
    }

    private void validator(TodoRegisterDto registerDto) {
        LocalDate now = LocalDate.parse(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        if (registerDto.getStartDate() == null) {
            throw new CustomException(ErrorCode.NO_INPUT_START_DATE);
        }
        if (registerDto.getEndDate() == null) {
            throw new CustomException(ErrorCode.NO_INPUT_END_DATE);
        }
        if (registerDto.getContent() == null) {
            throw new CustomException(ErrorCode.NO_INPUT_CONTENT);
        }
        if (registerDto.getContent().equals("")) {
            throw new CustomException(ErrorCode.NO_INPUT_CONTENT);
        }
        if (registerDto.getDifficulty() == 0) {
            throw new CustomException(ErrorCode.NO_INPUT_DIFFICULTY);
        }
        if (registerDto.getTodoType() == 0) {
            throw new CustomException(ErrorCode.NO_INPUT_TODO_TYPE);
        }
        if (registerDto.getStartDate().isBefore(now)) {
            throw new CustomException(ErrorCode.BAD_REQUEST_START_DATE);
        }
        if (registerDto.getEndDate().isBefore(registerDto.getStartDate())) {
            throw new CustomException(ErrorCode.BAD_REQUEST_END_DATE);
        }
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

    //일간 통계
    @Transactional
    public StatisticsResponseDto getStatistics(UserDetailsImpl userDetails) {
        LocalDate rNow = LocalDate.parse(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        LocalDate now = LocalDate.parse(LocalDate.now().minusDays(1)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        LocalDate start = now.minusDays(6);

        Pageable pageable = PageRequest.of(0, 7, Sort.Direction.ASC, "completionDate");
        StatisticsResponseDto responseDto = new StatisticsResponseDto();

        //내 매칭상태 조회
        Member member = getMember(userDetails.getMember().getId());
        Boolean matchingState = member.getMatchingState();
        responseDto.setMyMatchingState(matchingState);


        //Map 초기화
        Map<String, Long> myDateAndTodoCntMap = new LinkedHashMap<>();
        Map<String, Integer> myDateAndDifSumMap = new LinkedHashMap<>();
        Map<String, Long> partnerDateAndTodoCntMap = new LinkedHashMap<>();
        Map<String, Integer> partnerDateAndTodoDifSumMap = new LinkedHashMap<>();
        for (int i = 7; i >= 1; i--) {
            LocalDate d = rNow.minusDays(i);
            myDateAndTodoCntMap.put(String.valueOf(d), 0L);
            myDateAndDifSumMap.put(String.valueOf(d), 0);
            partnerDateAndTodoCntMap.put(String.valueOf(d), 0L);
            partnerDateAndTodoDifSumMap.put(String.valueOf(d), 0);
        }

        //내 투두 갯수 및 날짜
        List<TodoDetailsResponseDto> todoDetailsList =
                todoRepository.findTodoDetails(start, now, userDetails.getMember().getId(), pageable);
        for (TodoDetailsResponseDto dto : todoDetailsList) {
            LocalDate date = dto.getDate();
            Long cnt = dto.getCnt();
            int exp = dto.getExp() * 5;

            myDateAndTodoCntMap.put(String.valueOf(date), cnt);
            myDateAndDifSumMap.put(String.valueOf(date), exp);
        }
        responseDto.setMyAchievement(myDateAndTodoCntMap);
        responseDto.setMyExpChanges(myDateAndDifSumMap);


        //파트너 정보
        Matching matching = matchingRepository.getMatching(userDetails.getMember().getId()).orElse(null);
        if (matching != null) {
            Long searchedUserPartnerId = userDetails.equals(matching.getRequesterId())
                    ? matching.getRespondentId() : matching.getRequesterId();
            Member partner = memberRepository.findById(searchedUserPartnerId).orElseThrow(
                    () -> new CustomException(ErrorCode.NOT_FOUND_PARTNER));
            Long partnerId = partner.getId();

            //파트너 투두 갯수 및 날짜
            List<TodoDetailsResponseDto> partnerTodoDetailsList =
                    todoRepository.findTodoDetails(start, now, partnerId, pageable);
            for (TodoDetailsResponseDto dto : partnerTodoDetailsList) {
                LocalDate date = dto.getDate();
                Long cnt = dto.getCnt();
                int exp = dto.getExp() * 5;

                partnerDateAndTodoCntMap.put(String.valueOf(date), cnt);
                partnerDateAndTodoDifSumMap.put(String.valueOf(date), exp);
            }

            responseDto.setFriendAchievement(partnerDateAndTodoCntMap);
            responseDto.setFriendExpChanges(partnerDateAndTodoDifSumMap);
        }

        return responseDto;
    }

    //월간 통계
    @Transactional
    public StatisticsResponseDto getStatisticsMonthly(UserDetailsImpl userDetails) {

        LocalDate startMonth = LocalDate.parse(LocalDate.now()
                .minusMonths(7).withDayOfMonth(1)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        LocalDate lastMonth = LocalDate.parse(LocalDate.now()
                .minusMonths(1).withDayOfMonth(LocalDate.now().minusMonths(1).lengthOfMonth())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        Pageable pageable = PageRequest.of(0, 6, Sort.Direction.ASC, "completionDate");
        StatisticsResponseDto responseDto = new StatisticsResponseDto();

        //내 매칭상태 조회
        Member member = getMember(userDetails.getMember().getId());
        Boolean matchingState = member.getMatchingState();
        responseDto.setMyMatchingState(matchingState);

        //Map 초기화
        Map<String, Long> myDateAndTodoCntMap = new LinkedHashMap<>();
        Map<String, Integer> myDateAndDifSumMap = new LinkedHashMap<>();
        Map<String, Long> partnerDateAndTodoCntMap = new LinkedHashMap<>();
        Map<String, Integer> partnerDateAndTodoDifSumMap = new LinkedHashMap<>();
        for (int i = 6; i >= 1; i--) {
            String month = LocalDate.now().minusMonths(i).format(DateTimeFormatter.ofPattern("M"));
            myDateAndTodoCntMap.put(month, 0L);
            myDateAndDifSumMap.put(month, 0);
            partnerDateAndTodoCntMap.put(month, 0L);
            partnerDateAndTodoDifSumMap.put(month, 0);
        }

        //내 투두 갯수 및 날짜
        List<TodoDetailsResponseMonthlyDto> todoDetailsList =
                todoRepository.findTodoDetailsMonthly(startMonth, lastMonth, userDetails.getMember().getId(), pageable);
        for (TodoDetailsResponseMonthlyDto dto : todoDetailsList) {
            Integer date = dto.getDate();
            Long cnt = dto.getCnt();
            int exp = dto.getExp() * 5;

            myDateAndTodoCntMap.put(String.valueOf(date), cnt);
            myDateAndDifSumMap.put(String.valueOf(date), exp);
        }
        responseDto.setMyAchievement(myDateAndTodoCntMap);
        responseDto.setMyExpChanges(myDateAndDifSumMap);


        //파트너 정보
        Matching matching = matchingRepository.getMatching(userDetails.getMember().getId()).orElse(null);
        if (matching != null) {
            Long searchedUserPartnerId = userDetails.getMember().getId().equals(matching.getRequesterId())
                    ? matching.getRespondentId()
                    : matching.getRequesterId();
            Member partner = memberRepository.findById(searchedUserPartnerId).orElseThrow(
                    () -> new CustomException(ErrorCode.NOT_FOUND_PARTNER));
            Long partnerId = partner.getId();

            //파트너 투두 갯수 및 날짜
            List<TodoDetailsResponseMonthlyDto> partnerTodoDetailsList =
                    todoRepository.findTodoDetailsMonthly(startMonth, lastMonth, partnerId, pageable);
            for (TodoDetailsResponseMonthlyDto dto : partnerTodoDetailsList) {
                Integer date = dto.getDate();
                Long cnt = dto.getCnt();
                int exp = dto.getExp() * 5;

                partnerDateAndTodoCntMap.put(String.valueOf(date), cnt);
                partnerDateAndTodoDifSumMap.put(String.valueOf(date), exp);
            }

            responseDto.setFriendAchievement(partnerDateAndTodoCntMap);
            responseDto.setFriendExpChanges(partnerDateAndTodoDifSumMap);
        }

        return responseDto;

    }

    //주간 통계
    public StatisticsResponseDto getStatisticsWeekly(UserDetailsImpl userDetails) {

        //4주전 날짜 계산 : 일요일 기준으로 맞춤
        String sWeek = String.valueOf(LocalDate.now().minusWeeks(4));
        Calendar startWeek = Calendar.getInstance();
        String[] sDates = sWeek.split("-");
        int sYear = Integer.parseInt(sDates[0]);
        int sMonth = Integer.parseInt(sDates[1]);
        int sDay = Integer.parseInt(sDates[2]);
        startWeek.set(sYear, sMonth - 1, sDay);
        int sWeeksBefore = startWeek.get(Calendar.DAY_OF_WEEK);
        //일요일 구하기
        if (sWeeksBefore != 1) {
            startWeek.set(sYear, sMonth - 1, sDay - sWeeksBefore + 1);
        }

        //Calendar to LocalDate
        LocalDate startWeekMon = startWeek.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        Pageable pageable = PageRequest.of(0, 4, Sort.Direction.ASC, "completionDate");
        StatisticsResponseDto responseDto = new StatisticsResponseDto();

        //내 매칭상태 조회
        Member member = getMember(userDetails.getMember().getId());
        Boolean matchingState = member.getMatchingState();
        responseDto.setMyMatchingState(matchingState);

        //Map 초기화
        Map<String, Long> myDateAndTodoCntMap = new LinkedHashMap<>();
        Map<String, Integer> myDateAndDifSumMap = new LinkedHashMap<>();
        Map<String, Long> partnerDateAndTodoCntMap = new LinkedHashMap<>();
        Map<String, Integer> partnerDateAndTodoDifSumMap = new LinkedHashMap<>();

        //내 투두 갯수 및 날짜
        int j = 0;
        while (j < 4) {
            LocalDate start = startWeekMon.plusDays(7L * j);
            LocalDate last = startWeekMon.plusDays((7L * (j + 1)) - 1);
            TodoDetailsResponseWeeklyDto todoDetailsList = todoRepository.findTodoDetailsWeekly(
                    start, last, userDetails.getMember().getId(), pageable);

            Calendar cal = Calendar.getInstance();
            Date from = Date.from(start.atStartOfDay(ZoneId.systemDefault()).toInstant());
            cal.setTime(from);

            int nW = cal.get(Calendar.WEEK_OF_MONTH);
            int month = Integer.parseInt(String.valueOf(start).split("-")[1]);

            Long cnt = todoDetailsList.getCnt();
            int exp = todoDetailsList.getExp() * 5;

            myDateAndTodoCntMap.put(month + "월 " + nW + "주차", cnt);
            myDateAndDifSumMap.put(month + "월 " + nW + "주차", exp);

            responseDto.setMyAchievement(myDateAndTodoCntMap);
            responseDto.setMyExpChanges(myDateAndDifSumMap);

            j++;
        }

        //파트너 정보
        Matching matching = matchingRepository.getMatching(userDetails.getMember().getId()).orElse(null);
        if (matching != null) {
            Long searchedUserPartnerId = userDetails.getMember().getId().equals(matching.getRequesterId())
                    ? matching.getRespondentId()
                    : matching.getRequesterId();
            Member partner = memberRepository.findById(searchedUserPartnerId).orElseThrow(
                    () -> new CustomException(ErrorCode.NOT_FOUND_PARTNER));
            Long partnerId = partner.getId();

            //파트너 투두 갯수 및 날짜
            int k = 0;
            while (k < 4) {
                LocalDate start = startWeekMon.plusDays(7L * k);
                LocalDate last = startWeekMon.plusDays((7L * (k + 1)) - 1);
                TodoDetailsResponseWeeklyDto todoDetailsList = todoRepository.findTodoDetailsWeekly(
                        start, last, partnerId, pageable);

                Calendar cal = Calendar.getInstance();
                Date from = Date.from(start.atStartOfDay(ZoneId.systemDefault()).toInstant());
                cal.setTime(from);

                int nW = cal.get(Calendar.WEEK_OF_MONTH);
                int month = Integer.parseInt(String.valueOf(start).split("-")[1]);

                Long cnt = todoDetailsList.getCnt();
                int exp = todoDetailsList.getExp() * 5;

                partnerDateAndTodoCntMap.put(month + "월 " + nW + "주차", cnt);
                partnerDateAndTodoDifSumMap.put(month + "월 " + nW + "주차", exp);

                responseDto.setFriendAchievement(partnerDateAndTodoCntMap);
                responseDto.setFriendExpChanges(partnerDateAndTodoDifSumMap);

                k++;
            }
        }
        return responseDto;
    }
}