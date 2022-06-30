package com.hanghae.todoli.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TodoResponseDto {
    List<MatchingStateResponseDto> member;
//    List<TodoInfoDto> todos;
}
