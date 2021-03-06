package com.hanghae.todoli.matching.dto;

import com.hanghae.todoli.character.Dto.ThumbnailDto;
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
    private String thumbnailCharImg;
    private List<ThumbnailDto> equipItems;
}
