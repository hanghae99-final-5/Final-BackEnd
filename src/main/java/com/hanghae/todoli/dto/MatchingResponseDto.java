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

    private Boolean myMatchingState;
    private Long memberId;
    private String nickname;
    private Boolean partnerMatchingState;
    private String searchedUserPartner;
    private String charImg;
    private List<EquipItemDto> equipItems;
}
