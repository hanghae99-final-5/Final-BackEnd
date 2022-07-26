package com.hanghae.todoli.controller;


import com.hanghae.todoli.alarm.AlarmResponseDto;
import com.hanghae.todoli.alarm.AlarmService;
import com.hanghae.todoli.security.UserDetailsImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
    @ApiResponses({
            @ApiResponse(code=200, message="조회 성공"),
            @ApiResponse(code=400, message="실패"),
            @ApiResponse(code=403, message="Forbidden")
    })
    @ApiOperation(value = "알람 조회 메소드", notes = "자신의 알람창 조회 api 입니다.")
    @GetMapping("/api/alarms")
    public List<AlarmResponseDto> getAlarms(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return alarmService.getAlarms(userDetails);
    }

    //알람 삭제
    @ApiResponses({
            @ApiResponse(code=200, message="조회 성공"),
            @ApiResponse(code=400, message="실패"),
            @ApiResponse(code=403, message="Forbidden")
    })
    @ApiOperation(value = "알람 조회 메소드", notes = "자신의 알람창 조회 api 입니다.")
    @DeleteMapping("/api/alarms")
    public void deleteAlarms(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        alarmService.deleteAlarms(userDetails);
    }
}
