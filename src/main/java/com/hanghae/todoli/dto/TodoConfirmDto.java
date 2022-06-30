package com.hanghae.todoli.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Builder
public class TodoConfirmDto {
    private Long todoId;
    private Boolean confirmState;
}
