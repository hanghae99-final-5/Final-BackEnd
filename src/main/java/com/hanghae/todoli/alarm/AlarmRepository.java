package com.hanghae.todoli.alarm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    List<Alarm> findAllByMemberIdOrderByIdDesc(Long id);
    void deleteAllByMemberId(Long id);

    //매칭 수락
    @Query("select a from Alarm a join a.member m where m.id = :memberId and a.alarmType = 'ACCEPTANCE'")
    List<Alarm> findAllByAlarm(@Param("memberId")Long memberId);

    //인증해주기
    List<Alarm> findAllByTodoId(Long todoId);
}
