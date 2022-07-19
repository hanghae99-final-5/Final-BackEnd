package com.hanghae.todoli.matching.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MatchingStatePartnerDto {
    private Boolean matchingState;
    private Long partnerId;
}
