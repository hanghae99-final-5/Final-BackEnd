package com.hanghae.todoli.todo.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class TodoCompletionDto {
    private Long todoId;
    private Boolean completionState;
}
