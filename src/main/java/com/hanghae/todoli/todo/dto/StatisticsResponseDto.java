package com.hanghae.todoli.todo.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Builder
public class StatisticsResponseDto {
    Boolean myMatchingState;
    List<LocalDate>period;

    List<Long>myAchievement;
    List<Long>friendAchievement;

    List<Long>myExpChanges;
    List<Long>friendExpChanges;
}
