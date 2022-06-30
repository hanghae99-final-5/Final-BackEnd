package com.hanghae.todoli.dto;

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
