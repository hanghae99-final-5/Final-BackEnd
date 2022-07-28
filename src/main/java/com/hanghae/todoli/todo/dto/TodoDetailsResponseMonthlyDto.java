package com.hanghae.todoli.todo.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class TodoDetailsResponseMonthlyDto {
    Integer date;
    Long cnt;
    int exp;

    @QueryProjection
    public TodoDetailsResponseMonthlyDto(Integer date, Long cnt, int exp) {
        this.date = date;
        this.cnt = cnt;
        this.exp = exp;
    }
}
