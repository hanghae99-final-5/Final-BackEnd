package com.hanghae.todoli.repository;

import com.hanghae.todoli.models.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {


    List<Alarm> findAllByMemberId(Long id);
    void deleteAllByMemberId(Long id);
}
