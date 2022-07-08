package com.hanghae.todoli.alarm;

import com.hanghae.todoli.character.ThumbnailDto;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class AlarmResponseDto {
    private Long alarmId;

    private String message;

    private LocalDate alarmDate;

    private AlarmType alarmType;

    private Long alarmState;

    private Long senderId;

    private String thumbnailCharImg;

    private List<ThumbnailDto> senderEquipItems;
}