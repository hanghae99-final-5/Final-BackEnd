package com.hanghae.todoli.todo;

import com.hanghae.todoli.matching.MatchingStateResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TodoResponseDto {

    List<MatchingStateResponseDto> member;

    List<TodoInfoDto> todos;
}
