package com.hanghae.todoli.utils;

import com.hanghae.todoli.character.Character;
import com.hanghae.todoli.character.CharacterRepository;
import com.hanghae.todoli.member.Member;
import com.hanghae.todoli.member.MemberRepository;
import com.hanghae.todoli.todo.model.Todo;
import com.hanghae.todoli.todo.repository.TodoRepository;
import com.hanghae.todoli.todo.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component // 스프링이 필요 시 자동으로 생성하는 클래스 목록에 추가합니다.
@Transactional  //save 해줘야 하나?
public class Scheduler {
    private final TodoRepository todoRepository;
    private final CharacterRepository characterRepository;
    private final MemberRepository memberRepository;
    private final TodoService todoService;
    // 초, 분, 시, 일, 월, 주 순서
    @Scheduled(cron = "0 0 0 * * *")        // *은 상관없다는 뜻 저 코드는 새벽 12시에 매번 실행
    public void updatePrice() throws InterruptedException {
        System.out.println("투두 업데이트 실행");

        List<Todo> findAllTodo = todoRepository.findAll();
        for (Todo todo : findAllTodo) {
            LocalDate confirmDate = todo.getConfirmDate();
            Boolean confirmState = todo.getConfirmState();
            Character character = todo.getWriter().getCharacter();
            Boolean completionState = todo.getCompletionState();

            //현재 시간을 불러오는데, format해주면 string이 된다. 따라서 .parse를 사용하여 LocalDate 형식으로 맞춰준다.
            LocalDate now = LocalDate.parse(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

            //인증날짜가 현재 날짜보다 작은 경우. 즉 인증날짜가 지난 경우
            if (confirmDate.isBefore(now)) {
                if (!completionState) {   //완료상태가 false 일 때       true이면 유지되게 해야되는데 추가 기능
                    todo.completionState();
                } else {
                    todo.completionState();
                }
                //인증상태가 false.인증을 안한 상태
                if (!confirmState) {
                    character.minHpAndLv();
                }
            }
        }
    }


//    @Scheduled(cron = "0 0 0 * * *")
//    public void updateTodyExp() throws InterruptedException {
//        System.out.println("EXP 업데이트 실행");
//
//        List<Member> memResult = memberRepository.findAll();
//        for (Member member : memResult) {
//            Long charId = member.getCharacter().getId();                // member 1, 2, 3, 4, 5
//            Character character = characterRepository.findById(charId).orElseThrow(() -> new IllegalArgumentException("캐릭터가 존재하지 않습니다."));
//            int exp = character.getExp();
//            int level = character.getLevel();
//
//            /*
//            * 22일 기준
//            * 21일 데이터를 갱신
//            * 20일의 데이터와 21일의 데이터로 갱신
//            * 21일에는 지금 현재의 레벨과 경험치를 계산해서 total에 넣는다.
//            * 21일에 저장된 total - 20일기준 total을 빼준 후 setTodatExp에 넣어준다. -> 오늘의 변동량(21일)
//            * */
//            int myAllExp = (level * 100) + exp;        //시간 딱 넘어간 현재
//
//            int nowExp = (myAllExp) - character.getStackedExp();     //변동량
//            character.setDailyExp(nowExp);
//            character.setStackedExp(myAllExp);
//        }
//    }
}
