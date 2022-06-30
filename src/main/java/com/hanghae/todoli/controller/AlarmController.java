package com.hanghae.todoli.controller;

import com.hanghae.todoli.dto.AlarmResponseDto;
import com.hanghae.todoli.security.jwt.UserDetailsImpl;
import com.hanghae.todoli.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;

    //자신의 알람 보여주기
    @GetMapping("/api/alarms")
    public List<AlarmResponseDto> getAlarms(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return alarmService.getAlarms(userDetails);
    }

    //알람 삭제
    @DeleteMapping("/api/alarms")
    public void deleteAlarms(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        alarmService.deleteAlarms(userDetails);
    }
}
