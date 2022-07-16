package com.hanghae.todoli.todo.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TodoModifyDto {
    private String content;
    private int difficulty;
    private int todoType;
}
