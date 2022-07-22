package com.hanghae.todoli.todo.dto;

import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Builder
public class TodoDetailsResponseDto {
    Long cnt;
    Long exp;
}
