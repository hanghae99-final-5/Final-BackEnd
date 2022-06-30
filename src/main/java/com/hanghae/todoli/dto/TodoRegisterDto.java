package com.hanghae.todoli.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TodoRegisterDto {

    private String content;

    private String startDate;

    private String endDate;

    private int difficulty;

    // 투두 등록 Dto
    @Builder
    public TodoRegisterDto(String content, String startDate, String endDate, int difficulty) {
        this.content = content;
        this.startDate = startDate;
        this.endDate = endDate;
        this.difficulty = difficulty;
    }
}
