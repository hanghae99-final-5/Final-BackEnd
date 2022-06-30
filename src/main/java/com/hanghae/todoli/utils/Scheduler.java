package com.hanghae.todoli.utils;

import com.hanghae.todoli.models.Alarm;
import com.hanghae.todoli.models.Character;
import com.hanghae.todoli.models.Todo;
import com.hanghae.todoli.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Component // 스프링이 필요 시 자동으로 생성하는 클래스 목록에 추가합니다.
@Transactional  //save 해줘야 하나?
public class Scheduler {
    private final TodoRepository todoRepository;

    // 초, 분, 시, 일, 월, 주 순서
    @Scheduled(cron = "0 0 0 * * *")        // *은 상관없다는 뜻 저 코드는 새벽 1시에 매번 실행
    public void updatePrice() throws InterruptedException {
        System.out.println("투두 업데이트 실행");

        List<Todo> findAllTodo = todoRepository.findAll();
        for(Todo todo : findAllTodo){
            LocalDate confirmDate = todo.getConfirmDate();
            Boolean confirmState = todo.getConfirmState();
            Character character = todo.getWriter().getCharacter();
            Boolean completionState = todo.getCompletionState();

            //현재 시간을 불러오는데, format해주면 string이 된다. 따라서 .parse를 사용하여 LocalDate 형식으로 맞춰준다.
            LocalDate now = LocalDate.parse(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

            //인증날짜가 현재 날짜보다 작은 경우. 즉 인증날짜가 지난 경우
            if(confirmDate.isBefore(now)){
                //인증상태가 false.인증을 안한 상태
                if(!confirmState){
                    character.minHpAndLv();
                }else{  //인증상태는 true
                    if(!completionState){   //완료상태가 false 일 때       true이면 유지되게 해야되는데 추가 기능
                        todo.completionState();
                    }else{
                        todo.completionState();
                    }
                }

            }
        }
    }
}
