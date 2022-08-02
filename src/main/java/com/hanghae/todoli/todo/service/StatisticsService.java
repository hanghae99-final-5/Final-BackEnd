package com.hanghae.todoli.todo.service;

import com.hanghae.todoli.exception.CustomException;
import com.hanghae.todoli.exception.ErrorCode;
import com.hanghae.todoli.matching.Matching;
import com.hanghae.todoli.matching.MatchingRepository;
import com.hanghae.todoli.member.Member;
import com.hanghae.todoli.member.MemberRepository;
import com.hanghae.todoli.security.UserDetailsImpl;
import com.hanghae.todoli.todo.dto.StatisticsResponseDto;
import com.hanghae.todoli.todo.dto.TodoDetailsResponseDto;
import com.hanghae.todoli.todo.dto.TodoDetailsResponseMonthlyDto;
import com.hanghae.todoli.todo.dto.TodoDetailsResponseWeeklyDto;
import com.hanghae.todoli.todo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RequiredArgsConstructor
@Service
public class StatisticsService {

    private final TodoRepository todoRepository;
    private final MemberRepository memberRepository;
    private final MatchingRepository matchingRepository;

    //일간 통계
    @Transactional
    public StatisticsResponseDto getStatisticsDaily(UserDetailsImpl userDetails) {
        LocalDate lastDate = LocalDate.parse(LocalDate.now().minusDays(1)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        LocalDate startDate = lastDate.minusDays(6);

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
        for (int i = 6; i >= 0; i--) {
            LocalDate d = lastDate.minusDays(i);
            myDateAndTodoCntMap.put(String.valueOf(d), 0L);
            myDateAndDifSumMap.put(String.valueOf(d), 0);
            partnerDateAndTodoCntMap.put(String.valueOf(d), 0L);
            partnerDateAndTodoDifSumMap.put(String.valueOf(d), 0);
        }

        //내 투두 갯수 및 날짜
        List<TodoDetailsResponseDto> todoDetailsList =
                todoRepository.findTododetailsDaily(startDate, lastDate, userDetails.getMember().getId(), pageable);
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
                    todoRepository.findTododetailsDaily(startDate, lastDate, partnerId, pageable);
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

    //월간 통계
    @Transactional
    public StatisticsResponseDto getStatisticsMonthly(UserDetailsImpl userDetails) {

        LocalDate startMonth = LocalDate.parse(LocalDate.now()
                .minusMonths(6).withDayOfMonth(1)
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
            myDateAndTodoCntMap.put(month +"월", 0L);
            myDateAndDifSumMap.put(month +"월", 0);
            partnerDateAndTodoCntMap.put(month +"월", 0L);
            partnerDateAndTodoDifSumMap.put(month +"월", 0);
        }

        //내 투두 갯수 및 날짜
        List<TodoDetailsResponseMonthlyDto> todoDetailsList =
                todoRepository.findTodoDetailsMonthly(startMonth, lastMonth, userDetails.getMember().getId(), pageable);
        for (TodoDetailsResponseMonthlyDto dto : todoDetailsList) {
            Integer date = dto.getDate();
            Long cnt = dto.getCnt();
            int exp = dto.getExp() * 5;

            myDateAndTodoCntMap.put(date +"월", cnt);
            myDateAndDifSumMap.put(date +"월", exp);
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

                partnerDateAndTodoCntMap.put(date +"월", cnt);
                partnerDateAndTodoDifSumMap.put(date +"월", exp);
            }

            responseDto.setFriendAchievement(partnerDateAndTodoCntMap);
            responseDto.setFriendExpChanges(partnerDateAndTodoDifSumMap);
        }

        return responseDto;

    }

    private Member getMember(Long id) {
        return memberRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_MEMBER)
        );
    }
}
