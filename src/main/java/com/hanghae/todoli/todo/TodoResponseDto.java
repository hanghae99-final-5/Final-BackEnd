package com.hanghae.todoli.todo;

import com.hanghae.todoli.matching.MatchingStateResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TodoResponseDto {
    //  매칭 상태 List
    List<MatchingStateResponseDto> member;
    //  Todo List
    List<TodoInfoDto> todos;
}
