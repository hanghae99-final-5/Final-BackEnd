package com.hanghae.todoli.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TodoInfoDto {
    private Long todoId;
    private String content;
    private String proofImg;
    private String startDate;
    private String endDate;
    private int difficulty;
    private Boolean confirmState;
    private Boolean completionState;



}
