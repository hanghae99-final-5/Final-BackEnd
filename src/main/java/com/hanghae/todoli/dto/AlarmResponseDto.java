package com.hanghae.todoli.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class AlarmResponseDto {
    private Long alarmId;

    private String message;

    private LocalDate alarmDate;

    private Long senderId;
}
