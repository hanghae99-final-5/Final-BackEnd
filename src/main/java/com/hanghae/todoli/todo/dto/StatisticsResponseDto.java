package com.hanghae.todoli.todo.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Builder
public class StatisticsResponseDto {
    Boolean myMatchingState;


    Map<LocalDate, Long>myAchievement;
    Map<LocalDate, Long>friendAchievement;

    Map<LocalDate, Integer>myExpChanges;
    Map<LocalDate, Integer>friendExpChanges;
}
