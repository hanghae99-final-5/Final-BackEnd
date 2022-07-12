package com.hanghae.todoli.character.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FooterResponseDto {

    private String thumbnailCharImg;
    private Long myId;
    private String myNickname;
    private List<ThumbnailDto> myEquipItems;
    private Long partnerId;
    private String partnerNickname;
    private List<ThumbnailDto> partnerEquipItems;
}
