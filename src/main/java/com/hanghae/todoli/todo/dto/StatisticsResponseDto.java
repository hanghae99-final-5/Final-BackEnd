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


    Map<String, Long>myAchievement;
    Map<String, Long>friendAchievement;

    Map<String, Integer>myExpChanges;
    Map<String, Integer>friendExpChanges;
}
