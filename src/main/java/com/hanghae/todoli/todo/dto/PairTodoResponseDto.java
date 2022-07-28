package com.hanghae.todoli.todo.dto;

import com.hanghae.todoli.matching.dto.MatchingStatePartnerDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PairTodoResponseDto {
    List<MatchingStatePartnerDto> member;
    List<TodoInfoDto> todos;
}
