package com.hanghae.todoli.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AlarmResponseDto {
    private Long alarmId;

    private String message;

    private String alarmDate;

    private Long senderId;
}
