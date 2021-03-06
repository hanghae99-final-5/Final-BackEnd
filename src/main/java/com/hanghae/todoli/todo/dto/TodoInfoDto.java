package com.hanghae.todoli.todo.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Builder
public class TodoInfoDto {
    private Long todoId;

    private String content;

    private String proofImg;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate confirmDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdAt;

    private int difficulty;

    private Boolean confirmState;

    private Boolean completionState;

    private int todoType;
}
