package com.hanghae.todoli.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Builder
public class CharResponseDto {
    private Boolean matchingState;

    private int level;
    private int hp;
    private int maxHp;
    private int exp;
    private int maxExp;
    private int money;
    private String charImg;

    private List<EquipItemListDto> listDto;  //List<EquipItemListDto> listDto

    //상대방 캐릭터 상태
    @Data
    @Builder
    public static class PartnerDto{
        private Long memberId;
        private String nickname;
        private String charImg;

        private List<EquipItemListDto> listDto;
    }
}
