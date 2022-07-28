package com.hanghae.todoli.todo.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class TodoDetailsResponseWeeklyDto {
    Long cnt;
    int exp;

    @QueryProjection
    public TodoDetailsResponseWeeklyDto(Long cnt, int exp) {
        this.cnt = cnt;
        this.exp = exp;
    }
}
