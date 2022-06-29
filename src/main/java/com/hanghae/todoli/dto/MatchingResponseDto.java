package com.hanghae.todoli.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MatchingResponseDto {

    private Long memberId;
    private String nickname;
    private Boolean matchingState;
    private String charImg;
    private List<EquipItemDto> equipItems;
}
