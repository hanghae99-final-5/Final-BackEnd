package com.hanghae.todoli.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component // 스프링이 필요 시 자동으로 생성하는 클래스 목록에 추가합니다.
public class Scheduler {

    // 초, 분, 시, 일, 월, 주 순서
    @Scheduled(cron = "0 0 1 * * *")        // *은 상관없다는 뜻 저 코드는 새벽 1시에 매번 실행
    public void updatePrice() throws InterruptedException {
        System.out.println("투두 업데이트 실행");


    }

}
