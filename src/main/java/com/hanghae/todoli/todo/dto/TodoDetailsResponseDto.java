package com.hanghae.todoli.todo.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.time.LocalDate;


@NoArgsConstructor
@Getter @Setter
public class TodoDetailsResponseDto {

    LocalDate date;
    Long cnt;

    int exp;

    @QueryProjection
    public TodoDetailsResponseDto(LocalDate date, Long cnt, int exp) {
        this.date = date;
        this.cnt = cnt;
        this.exp = exp;
    }

}
