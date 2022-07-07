package com.hanghae.todoli.todo;

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
