package com.hanghae.todoli.repository;

import com.hanghae.todoli.models.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
}
