package com.hanghae.todoli.todo;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Builder
public class TodoConfirmDto {
    private Long todoId;
    private Boolean confirmState;
}
