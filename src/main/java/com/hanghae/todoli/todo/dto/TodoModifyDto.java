package com.hanghae.todoli.todo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TodoModifyDto {
    private String content;
    private int difficulty;
    private int todoType;
}
