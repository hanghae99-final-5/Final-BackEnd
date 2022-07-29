package com.hanghae.todoli.member.dto;

import com.hanghae.todoli.character.Dto.ThumbnailDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RankingDto {
    private Long memberId;
    private String nickname;
    private String thumbnailCharImg;
    private List<ThumbnailDto> equipItems;
}
