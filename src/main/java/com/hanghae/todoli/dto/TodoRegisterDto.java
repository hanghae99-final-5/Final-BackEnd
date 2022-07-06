package com.hanghae.todoli.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class TodoRegisterDto {

    private String content;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private int difficulty;

    private int todoType;

    // 투두 등록 Dto
    @Builder
    public TodoRegisterDto(String content, LocalDate startDate, LocalDate endDate, int difficulty, int todoType) {
        this.content = content;
        this.startDate = startDate;
        this.endDate = endDate;
        this.difficulty = difficulty;
        this.todoType = todoType;
    }
}
